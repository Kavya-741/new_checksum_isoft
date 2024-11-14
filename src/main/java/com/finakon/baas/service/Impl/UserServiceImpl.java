package com.finakon.baas.service.Impl;

import java.sql.Timestamp;
import java.util.*;
import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.LogoutRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserSession;
import com.finakon.baas.entities.UserSessionId;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.jwthelper.JwtTokenUtil;
import com.finakon.baas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.finakon.baas.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private GeneralParameterRepository generalParameterRepository;

    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;

    @Value("${pwd_min_length}")
    private Integer passwordMinLength;

    @Value("${ped_max_length}")
    private Integer passwordMaxLength;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public String validateCredentials(Integer legalEntityCode, String username, String password) {
        List<String> userIds = userRepository.countByLegalEntityCodeAndLoginIdAndPasswordAndStatusAndEntityStatus(
                legalEntityCode,
                username, password);
        if (!userIds.isEmpty()) {
            return userIds.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean validateCaptcha(String captcha) {

        final RestTemplate restTemplate = new RestTemplate();
        final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("secret", recaptchaSecretKey);
        requestBody.add("response", captcha);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, requestBody, Map.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map responseBody = responseEntity.getBody();
            Boolean success = (Boolean) responseBody.get(Constants.SUCCESS);
            return success != null && success;
        } else {
            return false;
        }
    }

    @Override
    public void updateUserLogoutSession(Integer legalEntityCode, String userId) {
        userSessionRepository.updateUserLogoutSession(false, userId, legalEntityCode);
    }

    @Override
    public Boolean isUserLocked(String userId, Integer legalEntityCode, boolean isLocked) {
        UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCodeAndIsLocked(userId,
                legalEntityCode, isLocked);
        if (userSession != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean isUserAlreadyLoggedIn(Integer legalEntityCode, String userId, Integer isLoggedIn) {
        UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCodeAndIsLogged(userId, legalEntityCode, true);
        if (userSession != null) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public User createUserSession(String userId, Integer legalEntityCode) {
        User user = userRepository.findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(userId, legalEntityCode, "A",
                "A");
        initialUserSessionCreate(legalEntityCode, user.getUserId(), true);
        return user;
    }

    @Override
    public User getLoggedInUser(String userId, Integer legalEntityCode) {
        return userRepository.findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(userId, legalEntityCode, "A",
                "A");
    }

    @Override
    public Boolean updateUserSession(Integer legalEntityCode, String userId) {
        Boolean isLocked = false;
        List<String> user = userRepository.findByUserId(legalEntityCode, userId);
        if (!user.isEmpty()) {
            UserSessionId id = new UserSessionId(legalEntityCode, user.get(0));
            UserSession userSession = userSessionRepository.findById(id).orElse(null);
            if (userSession != null) {
                Integer dbCount = userSession.getNumberOfAttemptsToLogin();
                Integer loginAttempt = ++dbCount;
                GeneralParameter generalEntity = generalParameterRepository
                        .findByLegalEntityCodeAndKey1AndKey2(legalEntityCode, "UNSUCCESSFUL_ATTEMPT", "LOCK");
                Integer originalAttempt = Integer.parseInt(generalEntity.getValue());
                if (loginAttempt <= originalAttempt) {
                    if (loginAttempt == originalAttempt) {
                        userSession.setLocked(true);
                    }
                    userSession.setNumberOfAttemptsToLogin(loginAttempt++);
                    userSession.setLastSignIn(new Timestamp(new Date().getTime()));
                    userSessionRepository.save(userSession);
                } else {
                    if (isUserLocked(user.get(0), legalEntityCode, true).booleanValue()) {
                        isLocked = true;
                    }
                }
            } else {
                UserSession initialSession = new UserSession();
                initialSession.setLegalEntityCode(legalEntityCode);
                initialSession.setUserId(user.get(0));
                initialSession.setLogged(false);
                initialSession.setLocked(false);
                initialSession.setNumberOfAttemptsToLogin(1);
                userSessionRepository.save(initialSession);
            }
        }
        return isLocked;
    }

    private void initialUserSessionCreate(Integer legalEntityCode, String userId, boolean isLoggedValue) {
        UserSession userSession = new UserSession();
        userSession.setLegalEntityCode(legalEntityCode);
        userSession.setUserId(userId);
        userSession.setIpAddress(null);
        userSession.setLogged(isLoggedValue);
        userSession.setLocked(false);
        userSession.setNumberOfAttemptsToLogin(0);
        userSession.setLastSignIn(new Timestamp(new Date().getTime()));
        userSession.setPingTime(null);
        userSessionRepository.save(userSession);
    }

    @Override
    public ResponseEntity<Object> login(MaintLegalEntity maintLegalEntity, LoginRequest loginRequest) {

        Map<String, Object> response = new HashMap<>();

        // validate the user
        String userId = validateCredentials(maintLegalEntity.getLegalEntityCode(), loginRequest.getUsername(),
                loginRequest.getPassword());

        // validate captcha
        boolean validCaptcha = false;
        if (maintLegalEntity.isEnableCaptcha()) {
            validCaptcha = validateCaptcha(loginRequest.getCaptcha());
        } else {
            validCaptcha = true;
        }

        if (!validCaptcha) {
            response.put(Constants.SUCCESS, false);
            response.put(Constants.MESSAGE, "Captcha invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        // if logout is false, when the user logged in and have not opted for prev
        // session logout

        if (userId != null) {
            // user is not a new login user
            // check if the user is locked or not

            UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCode(userId, maintLegalEntity.getLegalEntityCode());
            if (userSession != null && userSession.isLocked()) {
                response.put(Constants.SUCCESS, false);
                response.put(Constants.MESSAGE, "Your account is locked please reset password");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else if (userSession != null && userSession.isLogged()) {
                // see if there are any existing sessions active for the user
                response.put(Constants.SUCCESS, false);
                response.put(Constants.MESSAGE,
                        "Are you wanting to logout the user who is already logged into another system ?");
                GeneralParameter generalEntity = generalParameterRepository.findByLegalEntityCodeAndKey1AndKey2(maintLegalEntity.getLegalEntityCode(),
                Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);
                String token = JwtTokenUtil.generateTemporaryToken(maintLegalEntity, userId,
                        Integer.valueOf(generalEntity.getValue()));
                        response.put("token", token);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {

                createUserJwtToken(userId, maintLegalEntity, response);
                return ResponseEntity.ok(response);
            }
        } else {
            if (updateUserSession(maintLegalEntity.getLegalEntityCode(), loginRequest.getUsername()).booleanValue()) {
                response.put(Constants.SUCCESS, false);
                response.put(Constants.MESSAGE, "Your account is locked please reset password");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put(Constants.SUCCESS, false);
                response.put(Constants.MESSAGE, "Login Eception");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

    }

    public Map<String, Object> createUserJwtToken(String userId, MaintLegalEntity maintLegalEntity,
            Map<String, Object> response) {
        Integer legalEntityCode = maintLegalEntity.getLegalEntityCode();

        // create user session entry
        createUserSession(userId, legalEntityCode);

        // get the userRoles of the user and see if the role is a single role.

        // for a single role user we generate a permanent token

        List<String> userRolesList = userRoleMappingRepository.findRolesByUserId(userId, legalEntityCode);

        GeneralParameter generalEntity = generalParameterRepository.findByLegalEntityCodeAndKey1AndKey2(legalEntityCode,
                Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);

        if (userRolesList.size() == 1) {
            // generate permanent token
            String token = JwtTokenUtil.generateToken(userId, maintLegalEntity, userRolesList.get(0),
                    maintLegalEntity.getBusinessdateTimestamp(),
                    Integer.valueOf(generalEntity.getValue()));
            userSessionRepository.updateToken(token, userId, String.valueOf(legalEntityCode));
            response.put("roles", userRolesList.get(0));
            response.put("token", token);
        } else {
            String token = JwtTokenUtil.generateTemporaryToken(maintLegalEntity, userId,
                    Integer.valueOf(generalEntity.getValue()));
            userSessionRepository.updateToken(token, userId, String.valueOf(legalEntityCode));
            response.put("token", token);
        }
        response.put("tokenExpirationTimeInMinutes", Integer.valueOf(generalEntity.getValue()));
        response.put("userId", userId);
        response.put(Constants.MESSAGE, "Logged in successfully");
        response.put("success", true);
        return response;
    }

    public Map<String, Object> updateUserJwtToken(String userId, String roleId, MaintLegalEntity maintLegalEntity,
            Map<String, Object> response) {
        Integer legalEntityCode = maintLegalEntity.getLegalEntityCode();
        GeneralParameter generalEntity = generalParameterRepository.findByLegalEntityCodeAndKey1AndKey2(legalEntityCode,
                Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);
        if (!roleId.equals("") && roleId != null) {
            // generate permanent token
            String token = JwtTokenUtil.generateToken(userId, maintLegalEntity, roleId,
                    maintLegalEntity.getBusinessdateTimestamp(),
                    Integer.valueOf(generalEntity.getValue()));
            userSessionRepository.updateToken(token, userId, String.valueOf(legalEntityCode));
            response.put("roles", roleId);
            response.put("token", token);
        }
        response.put("tokenExpirationTimeInMinutes", Integer.valueOf(generalEntity.getValue()));
        response.put("userId", userId);
        response.put(Constants.MESSAGE, "Logged in successfully");
        response.put("success", true);
        return response;
    }

    @Override
    public ResponseEntity<Object> logout(MaintLegalEntity maintLegalEntity, LogoutRequest loginRequest) {

        Map<String, Object> response = new HashMap<>();

        // get the logged in the user
        User user = getLoggedInUser(loginRequest.getUsername(), maintLegalEntity.getLegalEntityCode());

        // if logout is true i.e user trying to login and opted for the prev session
        // close
        if (user != null) {
            updateUserLogoutSession(maintLegalEntity.getLegalEntityCode(), user.getUserId());
            response.put(Constants.SUCCESS, true);
            response.put(Constants.MESSAGE, "Successfully logged out");
            return ResponseEntity.ok(response);
        } else {
            response.put(Constants.SUCCESS, false);
            response.put(Constants.MESSAGE, "Logout Eception");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @Override
    public ResponseEntity<Object> updateTokenDetails(MaintLegalEntity maintLegalEntity,
            UpdateTokenDetailsRequest updateTokenDetailsRequest) {

        Map<String, Object> response = new HashMap<>();

        // get the userRole for the user
        List<String> userRoles = userRoleMappingRepository.findRolesByUserId(updateTokenDetailsRequest.getUsername(),
                maintLegalEntity.getLegalEntityCode());

        // if logout is true i.e user trying to login and opted for the prev session
        // close

        if (!userRoles.isEmpty() && userRoles.contains(updateTokenDetailsRequest.getRoleId())) {
            updateUserJwtToken(updateTokenDetailsRequest.getUsername(), updateTokenDetailsRequest.getRoleId(),
                    maintLegalEntity, response);
            response.put(Constants.SUCCESS, true);
            response.put(Constants.MESSAGE, "Successfully updated token details");
            return ResponseEntity.ok(response);
        } else {
            response.put(Constants.SUCCESS, false);
            response.put(Constants.MESSAGE, "Invalid User Role");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
