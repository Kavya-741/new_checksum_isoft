package com.bankaudit.service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.GeneralParameterDao;
import com.bankaudit.dao.MaintEntityDao;
import com.bankaudit.dao.UserDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.GetTokenDetailsDTO;
import com.bankaudit.dto.LoginRequest;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.dto.UpdateTokenDetailsRequest;
import com.bankaudit.dto.UserRegistrationDto;
import com.bankaudit.helper.Constants;
import com.bankaudit.helper.DomainUtil;
import com.bankaudit.jwthelper.JwtTokenUtil;
import com.bankaudit.model.GeneralParameter;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.model.MaintAuditTypeDesc;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.model.MaintLegalEntity;
import com.bankaudit.model.User;
import com.bankaudit.model.UserDeptMapping;
import com.bankaudit.model.UserDeptMappingHst;
import com.bankaudit.model.UserDeptMappingWrk;
import com.bankaudit.model.UserHst;
import com.bankaudit.model.UserRoleMapping;
import com.bankaudit.model.UserRoleMappingHst;
import com.bankaudit.model.UserRoleMappingWrk;
import com.bankaudit.model.UserSession;
import com.bankaudit.model.UserSessionId;
import com.bankaudit.model.UserWrk;
import com.bankaudit.repository.MaintLegalEntityRepository;
import com.bankaudit.repository.UserRepository;
import com.bankaudit.repository.UserRoleMappingRepository;
import com.bankaudit.repository.UserSessionRepository;

import io.jsonwebtoken.Claims;

@Service
@Transactional("transactionManager")
public class UserServiceImpl implements UserService {

    @Autowired
    MaintEntityService maintEntityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;

    @Autowired
    private MaintLegalEntityRepository maintLegalEntityRepository;

    @Autowired
    private MaintAuditTypeDescService maintAuditTypeDescService;


    @Autowired
    private MaintAuditSubgroupService maintAuditSubgroupService;

    @Autowired
    private MaintEntityAuditSubgroupMappingServiceImpl maintEntityAuditSubgroupMappingService;

    @Autowired
    private MaintUsergroupRolesService maintUsergroupRolesService;

    @Autowired
    MaintEntityDao maintEntityDao;

    @Autowired
	UserDao userDao;

    @Autowired
    GeneralParameterDao generalParameterDao;

    @Value("${pwd_min_length}")
    private Integer passwordMinLength;

    @Value("${ped_max_length}")
    private Integer passwordMaxLength;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ResponseEntity<ServiceStatus> login(String domain, LoginRequest loginRequest) {

        Map<String, Object> result = new HashMap<>();
        ServiceStatus apiResponse = new ServiceStatus();

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

            MaintEntity maintEntity = maintEntityDao.getUnique(maintLegalEntity.getLegalEntityCode(), user.getUnitCode(), BankAuditConstant.STATUS_ACTIVE);

            // general params for the token expiry time
            GeneralParameter generalEntity = generalParameterDao.findByLegalEntityCodeAndKey1AndKey2(maintLegalEntity.getLegalEntityCode(),Constants.JWT_TOKEN, Constants.ACCESS_TOKEN_TIMEOUT);

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
    public ResponseEntity<ServiceStatus> logout(String authTokenHeader) {
        ServiceStatus apiResponse = new ServiceStatus();

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
    public ResponseEntity<ServiceStatus> updateTokenDetails(String authTokenHeader,
            UpdateTokenDetailsRequest updateTokenDetailsRequest) {

        ServiceStatus apiResponse = new ServiceStatus();
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
            GeneralParameter generalEntity = generalParameterDao.findByLegalEntityCodeAndKey1AndKey2(
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
    public ResponseEntity<ServiceStatus> getUserRolesAndDetails(String authTokenHeader) {
        Map<String, Object> result = new HashMap<>();
        ServiceStatus apiResponse = new ServiceStatus();
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
            String userStatusDesc=("I".equalsIgnoreCase(userStatus)? "Inactive" : ("L".equalsIgnoreCase(userStatus)? "Locked": "C".equalsIgnoreCase(userStatus)? "Closed":"not active") );
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
                GeneralParameter generalEntity = generalParameterDao
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
	public DataTableResponse getUser(Integer legalEntityCode, String userId ,String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size) {
		List<String> unitLst=maintEntityService.getSubBranchesGyUserIdOrUnitId(legalEntityCode,"user",userId); // 
		return userDao.getUser( legalEntityCode, userId, search,  orderColumn,  orderDirection,
				page, size, unitLst); // UnitLst included to get the Users based on Jurisdiction
		
	}


	@Override
	public Boolean isUser(Integer legalEntityCode, String userId) {
		return userDao.isUser( legalEntityCode,  userId);
	}

    @Override
	public Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action) {
		boolean validUsr = true;
		if(role!=null)validUsr=userDao.isUserHavingEntitlement(legalEntityCode, userId, role, functionId, action);
		logger.info("isUserHavingParticularRole srvcImpl role is :: "+role +" validUser :: "+validUsr );
		return validUsr;
	}

    @Override
	public void createUser(UserRegistrationDto userRegistrationDto) {

		UserWrk userWrk=userRegistrationDto.getUserWrk();
		if(userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_DF)
				|| userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_UNAUTH)
				){
			
			userDao.save(userWrk);

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
						userDao.save(userRoleMappingWrk);	
					} 
			 }

			if(userRegistrationDto.getUserDeptIds()!=null){
				UserDeptMappingWrk userDeptMappingWrk = null;
				for (String userDeptId: userRegistrationDto.getUserDeptIds()) {
					userDeptMappingWrk=new UserDeptMappingWrk();
					userDeptMappingWrk.setUserId(userWrk.getUserId());
					userDeptMappingWrk.setStatus("A");
					userDeptMappingWrk.setLegalEntityCode(userWrk.getLegalEntityCode());
					userDeptMappingWrk.setEntityStatus("A");
					userDeptMappingWrk.setDepartmentCode(userDeptId);
					userDao.save(userDeptMappingWrk);
				}
			}
			
			 
		}else if (userWrk.getStatus().equalsIgnoreCase(BankAuditConstant.STATUS_AUTH)) {
			
			User user=new User();
			BeanUtils.copyProperties(userWrk, user);
			
			userDao.save(user);
			 
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
						userDao.save(userRoleMapping);	
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
					userDao.save(userDeptMapping);
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
						 maintEntityAuditSubgroupMapping.setId(user.getUserId());	
						 maintEntityAuditSubgroupMapping.setAuditTypeCode(audit.getAuditTypeCode());
						 maintEntityAuditSubgroupMapping.setAuditGroupCode(ags.get(0).getAuditGroupCode());
						 maintEntityAuditSubgroupMapping.setAuditSubGroupCode(ags.get(0).getAuditSubGroupCode());
						 maintEntityAuditSubgroupMapping.setStatus(user.getStatus());
						 maintEntityAuditSubgroupMapping.setMaker(user.getMaker());
						 maintEntityAuditSubgroupMapping.setMakerTimestamp(new Date());
						 userDao.save(maintEntityAuditSubgroupMapping);
					 }
				 }
			 }
			 // User to Audit mapping, End here
			 
		}
	}
	
	
	@Override
	public List<User> getUser(Integer legalEntityCode, String unitCode,String userId,String status) {
		List<User> users=userDao.getUser( legalEntityCode,  unitCode ,userId,status);
	 
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
				userWrk.setMakerTimestamp(new Date());
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
                    userHst.setMakerTimestamp(new Date());
					userDao.save(userHst);
					 if(userRoleMappings!=null){
						 UserRoleMappingHst userRoleMappingHst=null;
						 for (UserRoleMapping userRoleMapping : userRoleMappings) {
							 userRoleMappingHst=new UserRoleMappingHst();
							 BeanUtils.copyProperties(userRoleMapping, userRoleMappingHst);
							 userDao.save(userRoleMappingHst);
						}
					 }
					 if(userDeptMappings!=null){
						 UserDeptMappingHst userDeptMappingHst=null;
						 for (UserDeptMapping userDeptMapping : userDeptMappings) {
							 userDeptMappingHst=new UserDeptMappingHst();
							 BeanUtils.copyProperties(userDeptMapping, userDeptMappingHst);
							 userDao.save(userDeptMappingHst);
						}
					 }
					 
				}
			}
			
			// delete the existing work maintEntityAuditSubgroupMapping 
			//maintEntityAuditSubgroupMappingService.deleteMaintEntityAuditSubgroupMappingWrk(userWrk.getLegalEntityCode(),"U", userWrk.getUserId()); // Commented for BaaSDOS, as this feature taken out from here
			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			/*//delete the existing entity  
			userDao.deleteUser(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
			 */
			
			userDao.flushSession();
			// save again 
			userDao.saveOrUpdate(userWrk);
			 
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
						userDao.save(userRoleMappingWrk);	
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
					userDao.save(userDeptMappingWrk);
				}
			}
			
		}else{

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
				userDao.save(userHst);
				 
				 if(userRoleMappings!=null){
					 UserRoleMappingHst userRoleMappingHst=null;
					 for (UserRoleMapping userRoleMapping : userRoleMappings) {
						 userRoleMappingHst=new UserRoleMappingHst();
						 BeanUtils.copyProperties(userRoleMapping, userRoleMappingHst);
						 userDao.save(userRoleMappingHst);
					}
				 }

				if(userDeptMappings!=null){
					UserDeptMappingHst userDeptMappingHst=null;
					for (UserDeptMapping userDeptMapping : userDeptMappings) {
						userDeptMappingHst=new UserDeptMappingHst();
						BeanUtils.copyProperties(userDeptMapping, userDeptMappingHst);
						userDao.save(userDeptMappingHst);
					}
				}
				 
			}
			
			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			maintUsergroupRolesService.deleteUserRoleMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);
			maintUsergroupRolesService.deleteUserDeptMapping(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);

			//delete the existing user  from both the tables 
			/*userDao.deleteUser(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_AUTH);*/
			userDao.deleteUser(userWrk.getLegalEntityCode(),userWrk.getUserId(),BankAuditConstant.STATUS_UNAUTH);
					
			userDao.flushSession();
			
			String currentPassword=userDao.getUserPassword(userWrk.getLegalEntityCode(),userWrk.getUserId());
			User user=new User();
			
			BeanUtils.copyProperties(userWrk, user);
			user.setCheckerTimestamp(new Date());
			if(currentPassword!=null)user.setPassword(currentPassword);
			// save again 
			userDao.saveOrUpdate(user);
			 
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
						userDao.save(userRoleMapping);	
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
					userDao.save(userDeptMapping);
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
						 maintEntityAuditSubgroupMapping.setMakerTimestamp(new Date());
						 userDao.save(maintEntityAuditSubgroupMapping);
					 }
				 }
			 }
		}
	}

    @Override
	public List<User> getUsersByLevelAndNotInGrpEntityMapping(Integer legalEntityCode, String levelCode, String status) {
		List<User> users=userDao.getUsersByLevelAndNotInGrpEntityMapping( legalEntityCode,  levelCode ,status);
	 	return users ;
	}
}
