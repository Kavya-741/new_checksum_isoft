package com.finakon.baas.service.Impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.finakon.baas.dto.ApiResponse;
import com.finakon.baas.dto.GetTokenDetailsDTO;
import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.LogoutRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.MaintUsergroupRoles;
import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserRoleMapping;
import com.finakon.baas.entities.UserSession;
import com.finakon.baas.entities.UserSessionId;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.helper.DomainUtil;
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

import io.jsonwebtoken.Claims;

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

    @Autowired
    private MaintEntityRepository maintEntityRepository;

    @Autowired
    private MaintLegalEntityRepository maintLegalEntityRepository;

    @Value("${pwd_min_length}")
    private Integer passwordMinLength;

    @Value("${ped_max_length}")
    private Integer passwordMaxLength;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<ApiResponse> login(String domain, LoginRequest loginRequest) {

        Map<String, Object> result = new HashMap<>();
        ApiResponse apiResponse = new ApiResponse();

        // if domain is null and it is not dev env return invalid domain
        if (domain == null) {
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_DOMAIN);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        // find the legal entity code for the given domain
        MaintLegalEntity maintLegalEntity = DomainUtil.getLegalEntityCodeByDomain(domain);

        if (maintLegalEntity == null) {
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_DOMAIN);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        // validate captcha
        boolean validCaptcha = false;
        if (maintLegalEntity.isEnableCaptcha()) {
            validCaptcha = validateCaptcha(loginRequest.getCaptcha());
        } else {
            validCaptcha = true;
        }

        if (!validCaptcha) {
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_CAPTCHA);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        // validate the user
        User user = validateCredentials(maintLegalEntity.getLegalEntityCode(), loginRequest.getUsername(),
                loginRequest.getPassword());

        // if logout is false, when the user logged in and have not opted for prev
        // session logout

        if (user != null) {
            // user is not a new login user
            // check if the user is locked or not

            UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCode(user.getUserId(),
                    maintLegalEntity.getLegalEntityCode());
            // fetch the main entity for unit code and details
            MaintEntity maintEntity = maintEntityRepository
                    .findByLegalEntityCodeAndUnitCode(maintLegalEntity.getLegalEntityCode(), user.getUnitCode());

            // general params for the token expiry time
            GeneralParameter generalEntity = generalParameterRepository.findByLegalEntityCodeAndKey1AndKey2(
                    maintLegalEntity.getLegalEntityCode(),
                    Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);

            if (userSession != null && userSession.isLocked()) {
                apiResponse.setStatus(Constants.FAILURE);
                apiResponse.setMessage(Constants.UserControllerErrorCode.ACCOUNT_LOCKED);
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            } else if (userSession != null && userSession.isLogged()) {
                // see if there are any existing sessions active for the user
                apiResponse.setStatus(Constants.SUCCESS);
                apiResponse.setMessage(Constants.UserControllerErrorCode.EXISTING_SESSION);

                // generate the temporary token for the user
                String token = JwtTokenUtil.generateTemporaryToken(maintLegalEntity, user, maintEntity,
                        Integer.valueOf(generalEntity.getValue()));

                result.put("tokenExpirationTimeInMinutes", Integer.valueOf(generalEntity.getValue()));
                result.put("userId", user.getUserId());
                result.put("token", token);
                apiResponse.setResult(result);
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            } else {
                // create user session entry
                createUserSession(user.getUserId(), maintLegalEntity.getLegalEntityCode());

                // generate the temporary token for the user
                String token = JwtTokenUtil.generateTemporaryToken(maintLegalEntity, user, maintEntity,
                        Integer.valueOf(generalEntity.getValue()));

                apiResponse.setStatus(Constants.SUCCESS);
                apiResponse.setMessage(Constants.UserControllerErrorCode.LOGIN_SUCCESS);
                result.put("tokenExpirationTimeInMinutes", Integer.valueOf(generalEntity.getValue()));
                result.put("userId", user.getUserId());
                result.put("token", token);
                apiResponse.setResult(result);
                return ResponseEntity.ok(apiResponse);
            }
        } else {
            if (updateUserSession(maintLegalEntity.getLegalEntityCode(), loginRequest.getUsername()).booleanValue()) {
                apiResponse.setStatus(Constants.FAILURE);
                apiResponse.setMessage(Constants.UserControllerErrorCode.ACCOUNT_LOCKED);
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            } else {
                apiResponse.setStatus(Constants.FAILURE);
                apiResponse.setMessage(Constants.UserControllerErrorCode.LOGIN_EXCEPTION);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }
        }

    }

    @Override
    public ResponseEntity<ApiResponse> logout(String authTokenHeader) {
        ApiResponse apiResponse = new ApiResponse();

        String authToken = authTokenHeader.substring("Bearer ".length());
        Claims claims = JwtTokenUtil.decodeJwt(authToken);

        GetTokenDetailsDTO tokenDetails = JwtTokenUtil.getTokenDetails(claims);

        // get the logged in the user
        User user = getLoggedInUser(tokenDetails.getUserId(), tokenDetails.getLegalEntityCode());

        // if logout is true i.e user trying to login and opted for the prev session
        // close
        if (user != null) {
            updateUserLogoutSession(tokenDetails.getLegalEntityCode(), user.getUserId());
            apiResponse.setStatus(Constants.SUCCESS);
            apiResponse.setMessage(Constants.UserControllerErrorCode.LOGOUT_SUCCESS);
            return ResponseEntity.ok(apiResponse);
        } else {
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage(Constants.UserControllerErrorCode.LOGIN_EXCEPTION);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateTokenDetails(String authTokenHeader,
            UpdateTokenDetailsRequest updateTokenDetailsRequest) {

        ApiResponse apiResponse = new ApiResponse();
        // get User session details from jwt token
        String authToken = authTokenHeader.substring("Bearer ".length());
        Claims claims = JwtTokenUtil.decodeJwt(authToken);

        GetTokenDetailsDTO tokenDetails = JwtTokenUtil.getTokenDetails(claims);

        Map<String, Object> response = new HashMap<>();

        // get user existing roles
        List<UserRoleMapping> userRoles = userRoleMappingRepository
                .findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(tokenDetails.getUserId(),
                        tokenDetails.getLegalEntityCode(), BankAuditConstant.STATUS_AUTH,
                        BankAuditConstant.STATUS_ACTIVE);

        Optional<UserRoleMapping> userSelectedRole = userRoles.stream()
                .filter(e -> e.getUserRoleId().equalsIgnoreCase(updateTokenDetailsRequest.getRoleId())).findFirst();

        if (userSelectedRole.isPresent()) {

            // general params for the token expiry time
            GeneralParameter generalEntity = generalParameterRepository.findByLegalEntityCodeAndKey1AndKey2(
                    tokenDetails.getLegalEntityCode(),
                    Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);

            // get Maint Legal entity for jwt secret
            MaintLegalEntity maintLegalEntity = maintLegalEntityRepository
                    .findByLegalEntityCode(tokenDetails.getLegalEntityCode());

            String token = JwtTokenUtil.generatePermanentToken(maintLegalEntity, tokenDetails,
                    userSelectedRole.get().getUserRoleId(),
                    userSelectedRole.get().getMaintUsergroupRoles().getUgRoleName(),
                    Integer.valueOf(generalEntity.getValue()));

            claims = JwtTokenUtil.decodeJwt(token);
            // update the token in user session table
            userSessionRepository.updateToken(token, tokenDetails.getUserId(), tokenDetails.getLegalEntityCode());

            apiResponse.setStatus(Constants.SUCCESS);
            apiResponse.setMessage(Constants.UserControllerErrorCode.TOKEN_DETAILS_SUCCESS);
            response.put("tokenDetails", JwtTokenUtil.getTokenDetails(claims));
            response.put("token", token);
            apiResponse.setResult(response);
            return ResponseEntity.ok(apiResponse);
        } else {
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_ROLE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getUserRolesAndDetails(String authTokenHeader) {
        Map<String, Object> result = new HashMap<>();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus("success");
        apiResponse.setMessage("Valid user session.");

        // get User session details from jwt token
        String authToken = authTokenHeader.substring("Bearer ".length());
        Claims claims = JwtTokenUtil.decodeJwt(authToken);

        // fetch all the details from the jwt token
        GetTokenDetailsDTO tokenDetails = JwtTokenUtil.getTokenDetails(claims);
        result.put("tokenDetails", tokenDetails);

        // get user details
        User user = userRepository.findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(tokenDetails.getUserId(),
                tokenDetails.getLegalEntityCode(),
                BankAuditConstant.STATUS_AUTH, BankAuditConstant.STATUS_ACTIVE);
        if (BankAuditConstant.STATUS_ACTIVE.equalsIgnoreCase(user.getEntityStatus())) {// condition added to restrict
                                                                                       // the InActive and Closed Users

            // get user role mapping details
            List<UserRoleMapping> userRoles = userRoleMappingRepository
                    .findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(tokenDetails.getUserId(),
                            tokenDetails.getLegalEntityCode(), BankAuditConstant.STATUS_AUTH,
                            BankAuditConstant.STATUS_ACTIVE);
            result.put("roles",
                    userRoles.stream().map(UserRoleMapping::getMaintUsergroupRoles).collect(Collectors.toSet()));

            List<String> authorities = new ArrayList<>();

            userRoles.forEach((userRole) -> {
                authorities.add(userRole.getMaintUsergroupRoles().getUgRoleCode() + "-"
                        + userRole.getMaintUsergroupRoles().getUgRoleName());
            });

            result.put("authorities", authorities);
            apiResponse.setResult(result);
        } else {
            String userStatus = user.getEntityStatus();
            String userStatusDesc = Constants.UserControllerErrorCode.userStatusDesc.get(userStatus);
            apiResponse.setStatus(Constants.FAILURE);
            apiResponse.setMessage("The Account is " + userStatusDesc + ", contact Admin.");
        }

        return ResponseEntity.ok(apiResponse);
    }

    public User validateCredentials(Integer legalEntityCode, String username, String password) {
        return userRepository.findByLegalEntityCodeAndLoginIdAndPasswordAndStatusAndEntityStatus(
                legalEntityCode,
                username, password);
    }

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

    public void updateUserLogoutSession(Integer legalEntityCode, String userId) {
        userSessionRepository.updateUserLogoutSession(false, userId, legalEntityCode);
    }

    public Boolean isUserLocked(String userId, Integer legalEntityCode, boolean isLocked) {
        UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCodeAndIsLocked(userId,
                legalEntityCode, isLocked);
        if (userSession != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUserAlreadyLoggedIn(Integer legalEntityCode, String userId, Integer isLoggedIn) {
        UserSession userSession = userSessionRepository.findByUserIdAndLegalEntityCodeAndIsLogged(userId,
                legalEntityCode, true);
        if (userSession != null) {
            return true;
        } else {
            return false;
        }

    }

    public User createUserSession(String userId, Integer legalEntityCode) {
        User user = userRepository.findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(userId, legalEntityCode, "A",
                "A");
        initialUserSessionCreate(legalEntityCode, user.getUserId(), true);
        return user;
    }

    public User getLoggedInUser(String userId, Integer legalEntityCode) {
        return userRepository.findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(userId, legalEntityCode, "A",
                "A");
    }

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

}
