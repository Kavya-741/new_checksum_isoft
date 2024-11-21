package com.finakon.baas.service.Impl;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.finakon.baas.dto.Request.GetTokenDetailsDTO;
import com.finakon.baas.dto.Request.LoginRequest;
import com.finakon.baas.dto.Request.UpdateTokenDetailsRequest;
import com.finakon.baas.dto.Request.UserRegistrationDto;
import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintAuditSubgroup;
import com.finakon.baas.entities.MaintAuditTypeDesc;
import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.entities.MaintEntityAuditSubgroupMapping;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserDeptMapping;
import com.finakon.baas.entities.UserDeptMappingHst;
import com.finakon.baas.entities.UserDeptMappingWrk;
import com.finakon.baas.entities.UserHst;
import com.finakon.baas.entities.UserRoleMapping;
import com.finakon.baas.entities.UserRoleMappingHst;
import com.finakon.baas.entities.UserRoleMappingWrk;
import com.finakon.baas.entities.UserSession;
import com.finakon.baas.entities.UserWrk;
import com.finakon.baas.entities.IdClasses.MaintEntityAuditSubgroupMappingId;
import com.finakon.baas.entities.IdClasses.UserSessionId;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.helper.DomainUtil;
import com.finakon.baas.jwthelper.JwtTokenUtil;
import com.finakon.baas.repository.CustomRepositories.UserCustomRespository;
import com.finakon.baas.repository.JPARepositories.GeneralParameterRepository;
import com.finakon.baas.repository.JPARepositories.MaintEntityAuditSubgroupMappingRepository;
import com.finakon.baas.repository.JPARepositories.MaintEntityRepository;
import com.finakon.baas.repository.JPARepositories.MaintLegalEntityRepository;
import com.finakon.baas.repository.JPARepositories.UserDeptMappingHstRepository;
import com.finakon.baas.repository.JPARepositories.UserDeptMappingRepository;
import com.finakon.baas.repository.JPARepositories.UserDeptMappingWrkRepository;
import com.finakon.baas.repository.JPARepositories.UserHstRepository;
import com.finakon.baas.repository.JPARepositories.UserRepository;
import com.finakon.baas.repository.JPARepositories.UserRoleMappingHstRepository;
import com.finakon.baas.repository.JPARepositories.UserRoleMappingRepository;
import com.finakon.baas.repository.JPARepositories.UserRoleMappingWrkRepository;
import com.finakon.baas.repository.JPARepositories.UserSessionRepository;
import com.finakon.baas.repository.JPARepositories.UserWrkRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditSubgroupService;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;
import com.finakon.baas.service.ServiceInterfaces.MaintEntityService;
import com.finakon.baas.service.ServiceInterfaces.MaintUsergroupRolesService;
import com.finakon.baas.service.ServiceInterfaces.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;

@Service
@Transactional("transactionManager")
public class UserServiceImpl implements UserService {

    @Autowired
    MaintEntityService maintEntityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCustomRespository userCustomRespository;

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

    @Autowired
    private UserWrkRepository userWrkRepository;

    @Autowired
    private UserRoleMappingWrkRepository userRoleMappingWrkRepository;

    @Autowired
    private UserDeptMappingRepository userDeptMappingRepository;

    @Autowired
    private UserDeptMappingWrkRepository userDeptMappingWrkRepository;

    @Autowired
    private MaintAuditTypeDescService maintAuditTypeDescService;

    @Autowired
    private MaintEntityAuditSubgroupMappingRepository maintEntityAuditSubgroupMappingRepository;

    @Autowired
    private MaintAuditSubgroupService maintAuditSubgroupService;

    @Autowired
    private MaintEntityAuditSubgroupMappingServiceImpl maintEntityAuditSubgroupMappingService;

    @Autowired
    private MaintUsergroupRolesService maintUsergroupRolesService;

    @Autowired
    private UserHstRepository userHstRepository;

    @Autowired
    private UserRoleMappingHstRepository userRoleMappingHstRepository;

    @Autowired
    private UserDeptMappingHstRepository userDeptMappingHstRepository;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.bankaudit.service.UserService#getUser(java.lang.Integer,
     * java.lang.String, java.lang.String, java.lang.Integer, java.lang.String,
     * java.lang.Integer, java.lang.Integer)
     */
    @Override
    public DataTableResponse getUser(Integer legalEntityCode, String userId, String search, Integer orderColumn,
            String orderDirection,
            Integer page, Integer size) {
        List<String> unitLst = maintEntityService.getSubBranchesByUserIdOrUnitId(legalEntityCode, "user", userId); //
        return userCustomRespository.getUser(legalEntityCode, userId, search, orderColumn, orderDirection,
                page, size, unitLst); // UnitLst included to get the Users based on Jurisdiction

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bankaudit.service.UserService#isUser(java.lang.Integer,
     * java.lang.String)
     */
    @Override
    public Boolean isUser(Integer legalEntityCode, String userId) {
        return userCustomRespository.isUser(legalEntityCode, userId);
    }

    @Override
    public Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId,
            String action) {
        boolean validUsr = true;
        if (role != null)
            validUsr = userCustomRespository.isUserHavingEntitlement(legalEntityCode, userId, role, functionId, action);
        logger.info("isUserHavingParticularRole srvcImpl role is :: " + role + " validUser :: " + validUsr);
        return validUsr;
    }

    @Override
    public void createUser(UserRegistrationDto userRegistrationDto) {

        UserWrk userWrk = userRegistrationDto.getUserWrk();
        if (userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)
                || userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)) {

            userWrkRepository.save(userWrk);
            if (userRegistrationDto.getUserRoleIds() != null) {
                for (String userRoleId : userRegistrationDto.getUserRoleIds()) {
                    UserRoleMappingWrk userRoleMappingWrk = new UserRoleMappingWrk();
                    userRoleMappingWrk.setUserId(userWrk.getUserId());
                    userRoleMappingWrk.setStatus(BankAuditConstant.STATUS_ACTIVE);
                    userRoleMappingWrk.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
                    userRoleMappingWrk.setMaker(userWrk.getMaker());
                    userRoleMappingWrk.setLegalEntityCode(userWrk.getLegalEntityCode());
                    userRoleMappingWrk.setEntityStatus(BankAuditConstant.STATUS_ACTIVE);
                    userRoleMappingWrk.setUserRoleId(userRoleId);
                    userRoleMappingWrkRepository.save(userRoleMappingWrk);
                }
            }

            if (userRegistrationDto.getUserDeptIds() != null) {
                UserDeptMappingWrk userDeptMappingWrk = null;
                for (String userDeptId : userRegistrationDto.getUserDeptIds()) {
                    userDeptMappingWrk = new UserDeptMappingWrk();
                    userDeptMappingWrk.setUserId(userWrk.getUserId());
                    userDeptMappingWrk.setStatus("A");
                    userDeptMappingWrk.setLegalEntityCode(userWrk.getLegalEntityCode());
                    userDeptMappingWrk.setEntityStatus("A");
                    userDeptMappingWrk.setDepartmentCode(userDeptId);
                    userDeptMappingWrkRepository.save(userDeptMappingWrk);
                }
            }

        } else if (userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {

            User user = new User();
            BeanUtils.copyProperties(userWrk, user);

            userRepository.save(user);

            if (userRegistrationDto.getUserRoleIds() != null) {
                UserRoleMapping userRoleMapping = null;
                for (String userRoleId : userRegistrationDto.getUserRoleIds()) {
                    userRoleMapping = new UserRoleMapping();
                    userRoleMapping.setUserId(userWrk.getUserId());
                    userRoleMapping.setStatus("A");
                    userRoleMapping.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
                    userRoleMapping.setMaker(userWrk.getMaker());
                    userRoleMapping.setLegalEntityCode(userWrk.getLegalEntityCode());
                    userRoleMapping.setEntityStatus("A");
                    userRoleMapping.setUserRoleId(userRoleId);
                    userRoleMappingRepository.save(userRoleMapping);
                }
            }

            if (userRegistrationDto.getUserDeptIds() != null) {
                UserDeptMapping userDeptMapping = null;
                for (String userDeptId : userRegistrationDto.getUserDeptIds()) {
                    userDeptMapping = new UserDeptMapping();
                    userDeptMapping.setUserId(userWrk.getUserId());
                    userDeptMapping.setStatus("A");
                    userDeptMapping.setLegalEntityCode(userWrk.getLegalEntityCode());
                    userDeptMapping.setEntityStatus("A");
                    userDeptMapping.setDepartmentCode(userDeptId);
                    userDeptMappingRepository.save(userDeptMapping);
                }
            }

            // for BaaSDOS, mapping each Inspection type to User
            logger.info("User to Audit mapping .. " + user.getLegalEntityCode() + user.getUserId());
            List<MaintAuditTypeDesc> auditLst = maintAuditTypeDescService
                    .getMaintAuditTypeDescByLegalEntityCode(user.getLegalEntityCode());
            for (MaintAuditTypeDesc audit : auditLst) { // iterate all the list of Audit types
                List<MaintEntityAuditSubgroupMapping> userAuditMapping = maintEntityAuditSubgroupMappingService
                        .getMaintEntityAuditSubgroupMappingIdWithName(user.getLegalEntityCode(), "U", user.getUserId(),
                                audit.getAuditTypeCode(), "A"); // U-> User, A-> Authorize
                if (userAuditMapping.isEmpty()) { // If user to that Audit mapping not done, then Insert a new mapping
                    List<MaintAuditSubgroup> ags = maintAuditSubgroupService
                            .getMaintAuditSubgroupes(user.getLegalEntityCode(), audit.getAuditTypeCode(), null, null);
                    if (!ags.isEmpty()) {
                        MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping = new MaintEntityAuditSubgroupMapping();
                        maintEntityAuditSubgroupMapping.setLegalEntityCode(user.getLegalEntityCode());
                        maintEntityAuditSubgroupMapping.setId(user.getUserId());
                        maintEntityAuditSubgroupMapping.setAuditTypeCode(audit.getAuditTypeCode());
                        maintEntityAuditSubgroupMapping.setAuditGroupCode(ags.get(0).getAuditGroupCode());
                        maintEntityAuditSubgroupMapping.setAuditSubGroupCode(ags.get(0).getAuditSubGroupCode());
                        maintEntityAuditSubgroupMapping.setStatus(user.getStatus());
                        maintEntityAuditSubgroupMapping.setMaker(user.getMaker());
                        // maintEntityAuditSubgroupMapping.setMakerTimestamp(user.getBusinessdateTimestamp());
                        maintEntityAuditSubgroupMappingRepository.save(maintEntityAuditSubgroupMapping);
                    }
                }
            }
            // User to Audit mapping, End here

        }
    }

    @Override
	public List<User> getUser(Integer legalEntityCode, String unitCode,String userId,String status) {
		List<User> users=userCustomRespository.getUser( legalEntityCode,  unitCode ,userId,status);
	 
		if(users!=null && users.size()>0 && users.size()<=1){
			users.get(0).setMaintEntityAuditSubgroupMappings(maintEntityAuditSubgroupMappingService.getMaintEntityAuditSubgroupMapping(legalEntityCode, "U", userId,status));
		}
		return users ;
	}



    @Override
	public void updateUser(UserRegistrationDto userRegistrationDto) {

		UserWrk userWrk=userRegistrationDto.getUserWrk();

        
		
		if(!userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)){

			if(userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
					|| userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_MOD)
					|| userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)){

                MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(userWrk.getLegalEntityCode());

				userWrk.setMakerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
			}
			if(!userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)){
				// get Db object and save into the history
				List<User> userDbs=getUser(userWrk.getLegalEntityCode(), null, userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
				User userDb = null;
				if(userDbs!=null && userDbs.size() > 0){
					userDb = userDbs.get(0);
				}
				List<UserRoleMapping> userRoleMappings=maintUsergroupRolesService.getUserRoleMappings(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
				List<UserDeptMapping> userDeptMappings=maintUsergroupRolesService.getUserDeptMappings(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
				if(userDb!=null){
					UserHst userHst=new UserHst();
					BeanUtils.copyProperties(userDb, userHst);
					userHstRepository.save(userHst);
					
					 if(userRoleMappings!=null){
						 UserRoleMappingHst userRoleMappingHst=null;
						 for (UserRoleMapping userRoleMapping : userRoleMappings) {
							 userRoleMappingHst=new UserRoleMappingHst();
							 BeanUtils.copyProperties(userRoleMapping, userRoleMappingHst);
							 userRoleMappingHstRepository.save(userRoleMappingHst);
						}
					 }
					 if(userDeptMappings!=null){
						 UserDeptMappingHst userDeptMappingHst=null;
						 for (UserDeptMapping userDeptMapping : userDeptMappings) {
							 userDeptMappingHst=new UserDeptMappingHst();
							 BeanUtils.copyProperties(userDeptMapping, userDeptMappingHst);
							 userDeptMappingHstRepository.save(userDeptMappingHst);
						}
					 }
					 
				}
			}
			
			// delete the existing work maintEntityAuditSubgroupMapping 
			//maintEntityAuditSubgroupMappingService.deleteMaintEntityAuditSubgroupMappingWrk(userWrk.getLegalEntityCode(),"U", userWrk.getUserId()); // Commented for BaaSDOS, as this feature taken out from here
			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
			 
			 if(userRegistrationDto.getUserRoleIds()!=null){
				 UserRoleMappingWrk userRoleMappingWrk=null;	
				 for (String userRoleId: userRegistrationDto.getUserRoleIds()) {
						 userRoleMappingWrk=new UserRoleMappingWrk();
						 userRoleMappingWrk.setUserId(userWrk.getUserId());
						 userRoleMappingWrk.setStatus("A");
						 userRoleMappingWrk.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
						 userRoleMappingWrk.setMaker(userWrk.getMaker());
						 userRoleMappingWrk.setLegalEntityCode(userWrk.getLegalEntityCode());
						 userRoleMappingWrk.setEntityStatus("A");
						 userRoleMappingWrk.setUserRoleId(userRoleId);
						userRoleMappingWrkRepository.save(userRoleMappingWrk);	
					} 
			 }

			if(userRegistrationDto.getUserDeptIds()!=null){
				UserDeptMappingWrk userDeptMappingWrk=null;
				for (String userDeptId: userRegistrationDto.getUserDeptIds()) {
					userDeptMappingWrk=new UserDeptMappingWrk();
					userDeptMappingWrk.setUserId(userWrk.getUserId());
					userDeptMappingWrk.setStatus("A");
					userDeptMappingWrk.setLegalEntityCode(userWrk.getLegalEntityCode());
					userDeptMappingWrk.setEntityStatus("A");
					userDeptMappingWrk.setDepartmentCode(userDeptId);
					userDeptMappingWrkRepository.save(userDeptMappingWrk);
				}
			}
			
		}else{

            MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(userWrk.getLegalEntityCode());

			// get Db object and save into the history
			
			List<User> userDbs=getUser(userWrk.getLegalEntityCode(), null, userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			User userDb = null;
			if(userDbs!=null && userDbs.size() > 0){
				userDb = userDbs.get(0);
			}
			
			List<UserRoleMapping> userRoleMappings=maintUsergroupRolesService.getUserRoleMappings(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			List<UserDeptMapping> userDeptMappings=maintUsergroupRolesService.getUserDeptMappings(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			if(userDb!=null){
				UserHst userHst=new UserHst();
				BeanUtils.copyProperties(userDb, userHst);
				userHstRepository.save(userHst);
				 
				 if(userRoleMappings!=null){
					 UserRoleMappingHst userRoleMappingHst=null;
					 for (UserRoleMapping userRoleMapping : userRoleMappings) {
						 userRoleMappingHst=new UserRoleMappingHst();
						 BeanUtils.copyProperties(userRoleMapping, userRoleMappingHst);
						 userRoleMappingHstRepository.save(userRoleMappingHst);
					}
				 }

				if(userDeptMappings!=null){
					UserDeptMappingHst userDeptMappingHst=null;
					for (UserDeptMapping userDeptMapping : userDeptMappings) {
						userDeptMappingHst=new UserDeptMappingHst();
						BeanUtils.copyProperties(userDeptMapping, userDeptMappingHst);
						userDeptMappingHstRepository.save(userDeptMappingHst);
					}
				}
				 
			}

			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			//delete the existing user  from both the tables 
			/*userDao.deleteUser(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);*/

            User deleteUser = userRepository.findByUserIdAndLegalEntityCodeAndStatus(userWrk.getUserId(), userWrk.getLegalEntityCode(), BankAuditConstant.STATUS_UNAUTH);
			userRepository.delete(deleteUser);

			User existingUser=userRepository.findByUserIdAndLegalEntityCode(userWrk.getUserId(), userWrk.getLegalEntityCode());
			User user=new User();
			
			BeanUtils.copyProperties(userWrk, user);
			user.setCheckerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
			if(existingUser.getPassword()!=null)user.setPassword(existingUser.getPassword());
			// save again 
			userRepository.save(user);
			 
			 if(userRegistrationDto.getUserRoleIds()!=null){
				 UserRoleMapping userRoleMapping=null;	
				 for (String userRoleId: userRegistrationDto.getUserRoleIds()) {
					 userRoleMapping=new UserRoleMapping();
					 userRoleMapping.setUserId(userWrk.getUserId());
					 userRoleMapping.setStatus("A");
					 userRoleMapping.setMakerTimestamp(new Timestamp(System.currentTimeMillis()));
					 userRoleMapping.setMaker(userWrk.getMaker());
					 userRoleMapping.setLegalEntityCode(userWrk.getLegalEntityCode());
					 userRoleMapping.setEntityStatus("A");
					 userRoleMapping.setUserRoleId(userRoleId);
					userRoleMappingRepository.save(userRoleMapping);	
				} 
			 }

			if(userRegistrationDto.getUserDeptIds()!=null){
				UserDeptMapping userDeptMapping = null;
				for (String userDeptId: userRegistrationDto.getUserDeptIds()) {
					userDeptMapping=new UserDeptMapping();
					userDeptMapping.setUserId(userWrk.getUserId());
					userDeptMapping.setStatus("A");
					userDeptMapping.setLegalEntityCode(userWrk.getLegalEntityCode());
					userDeptMapping.setEntityStatus("A");
					userDeptMapping.setDepartmentCode(userDeptId);
					userDeptMappingRepository.save(userDeptMapping);
				}
			}
			 
			// for BaaSDOS, mapping each Inspection type to User
			 logger.info("User to Audit mapping .. "+user.getLegalEntityCode() + user.getUserId());
			 List<MaintAuditTypeDesc> auditLst= maintAuditTypeDescService.getMaintAuditTypeDescByLegalEntityCode(user.getLegalEntityCode());
			 for(MaintAuditTypeDesc audit: auditLst) { // iterate all the list of Audit types
				 List<MaintEntityAuditSubgroupMapping> userAuditMapping=maintEntityAuditSubgroupMappingService.getMaintEntityAuditSubgroupMappingIdWithName(user.getLegalEntityCode(), "U", user.getUserId(), audit.getAuditTypeCode(), "A");  // U-> User, A-> Authorize
				 if(userAuditMapping.isEmpty()) { // If user to that Audit mapping not done, then Insert a new mapping
					 List<MaintAuditSubgroup> ags = maintAuditSubgroupService.getMaintAuditSubgroupes(user.getLegalEntityCode(),audit.getAuditTypeCode(),null,null);
					 if(!ags.isEmpty()) {
						 MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping=new MaintEntityAuditSubgroupMapping();
						 maintEntityAuditSubgroupMapping.setLegalEntityCode(user.getLegalEntityCode());
						 maintEntityAuditSubgroupMapping.setMappingType("U");	
						 maintEntityAuditSubgroupMapping.setId(user.getUserId());	
						 maintEntityAuditSubgroupMapping.setAuditTypeCode(audit.getAuditTypeCode());
						 maintEntityAuditSubgroupMapping.setAuditGroupCode(ags.get(0).getAuditGroupCode());
						 maintEntityAuditSubgroupMapping.setAuditSubGroupCode(ags.get(0).getAuditSubGroupCode());
						 maintEntityAuditSubgroupMapping.setStatus(user.getStatus());
						 maintEntityAuditSubgroupMapping.setMaker(user.getMaker());
						 maintEntityAuditSubgroupMapping.setMakerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
						 maintEntityAuditSubgroupMappingRepository.save(maintEntityAuditSubgroupMapping);
					 }
				 }
			 }
			 // User to Audit mapping, End here
	
			 
		}
	}

}
