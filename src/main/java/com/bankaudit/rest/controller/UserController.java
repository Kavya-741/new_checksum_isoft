package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.LoginRequest;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.dto.UpdateTokenDetailsRequest;
import com.bankaudit.dto.UserRegistrationDto;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.GeneralParameter;
import com.bankaudit.model.User;
import com.bankaudit.model.UserWrk;
import com.bankaudit.service.GeneralParameterService;
import com.bankaudit.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private GeneralParameterService generalParameterService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private String functionId="USER";

	@CrossOrigin
	@GetMapping("/healthCheck")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Service is alive");
	}

	@CrossOrigin
	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceStatus> login(@RequestHeader(value = "Host", required = false) String domain,
			@RequestBody LoginRequest loginRequest) {
		return userService.login(domain, loginRequest);
	}

	@CrossOrigin
	@PostMapping(value = "/updateTokenDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceStatus> updateTokenDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
			@RequestBody UpdateTokenDetailsRequest updateTokenDetailsRequest) {
		return userService.updateTokenDetails(authorizationHeader, updateTokenDetailsRequest);
	}

	@CrossOrigin
	@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceStatus> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		return userService.logout(authorizationHeader);
	}

	@CrossOrigin
	@PostMapping(value = "/getUserRolesAndDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ServiceStatus> getUserRolesAndDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
		return userService.getUserRolesAndDetails(authorizationHeader);
	}


	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createUser(@RequestBody UserRegistrationDto userRegistrationDto,
			@RequestParam(required=false,value="legalEntityCode")String legalEntityCodeStr,
			@RequestParam(required=false,value="userCode")String userId,
			@RequestParam(required=false,value="role") String role){
		
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(userRegistrationDto!=null
				&& userRegistrationDto.getUserWrk()!=null
				&& userRegistrationDto.getUserWrk().getLegalEntityCode()!=null
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile1()) 
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile2()) 
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getLandline1())
				// all validations required 
				){
			try {
				logger.info("Inside User createUser() ..");
				boolean validUsrforRole=userService.isUserHavingEntitlement(Integer.parseInt(legalEntityCodeStr), userId, role, functionId, "CREATE"); // To validate if the User having the particular role for VAPT (Broken access control)
				if(validUsrforRole) {
					userRegistrationDto.getUserWrk().setMakerTimestamp(new Date());
					
					List<GeneralParameter> generalParameters=generalParameterService.getGeneralParameter(userRegistrationDto.getUserWrk().getLegalEntityCode(), null, null, BankAuditConstant.GP_DEFAULT_PASS,null, null, null);
					if(generalParameters!=null && !generalParameters.isEmpty()){
						userRegistrationDto.getUserWrk().setPassword(passwordEncoder.encode(generalParameters.get(0).getValue()));
						userService.createUser(userRegistrationDto);
						serviceStatus.setStatus("success");
						serviceStatus.setMessage("successfully registered");	
					}else {
						serviceStatus.setStatus("failure");
						serviceStatus.setMessage("default password not present in db");
					}
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("User is not entitled for this Role ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid user payload");
		}		
		return serviceStatus;
	}

	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getUser(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam("userId")String userId){
		
		DataTableResponse dataTableResponse=null;
		
		if(!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				Integer draw=Integer.parseInt(drawStr);
				Integer size=Integer.parseInt(length);
				Integer page=Integer.parseInt(start)/size;
				Integer orderColumn=null;
				if(!BankAuditUtil.isEmptyString(orderColumnStr)){
					orderColumn=Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = userService.getUser(legalEntityCode,userId,search,orderColumn,orderDirection,page,size);
				dataTableResponse.setDraw(draw);    
					
			} catch (Exception e) {
                dataTableResponse=new DataTableResponse();
                dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}
		
		}else {
			dataTableResponse=new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}		
		return dataTableResponse;
	}
	

	@GetMapping(value="/get",produces=MediaType.APPLICATION_JSON_VALUE) 
	ServiceStatus getUser(
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam(required=false,value="unitCode") String unitCode,
			@RequestParam(required=false,value="userCode") String userId,
			@RequestParam(value="status") String status
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		
		if(!BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				List<User> users = userService.getUser(legalEntityCode,unitCode,userId,status);
				if(users!=null && !users.isEmpty()){
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved");
					serviceStatus.setResult(users);
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("no records found");
				}
					
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}
		
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("legalEntityCode required");
		}
		
		
		return serviceStatus;
	}

	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateUser(@RequestBody UserRegistrationDto userRegistrationDto,
			@RequestParam(required=false,value="legalEntityCode")String legalEntityCodeStr,
			@RequestParam(required=false,value="userCode")String userId,
			@RequestParam(required=false,value="role") String role){
		
		ServiceStatus serviceStatus=new ServiceStatus();
		 logger.info("updateUser .. "+ userRegistrationDto.getUserWrk().getMobile1() +" M2.. "+userRegistrationDto.getUserWrk().getMobile2() +" L1.. "+userRegistrationDto.getUserWrk().getLandline1());
		if(userRegistrationDto!=null
				&& userRegistrationDto.getUserWrk()!=null
               && userRegistrationDto.getUserWrk().getLegalEntityCode()!=null
               && !BankAuditUtil.isEmptyString(userRegistrationDto.getUserWrk().getUserId())
               && BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile1()) 
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile2()) 
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getLandline1())
				){
			try {
				boolean validUsrforRole=userService.isUserHavingEntitlement(Integer.parseInt(legalEntityCodeStr), userId, role, functionId, "UPDATE"); // To validate if the User having the particular role for VAPT (Broken access control)
				if(validUsrforRole) {
					UserWrk user=userRegistrationDto.getUserWrk();
					if(userRegistrationDto.getUserWrk().getEntityStatus().equalsIgnoreCase("A")) userRegistrationDto.getUserWrk().setUnsuccessfulAttempts(0);
					if(user.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
						user.setCheckerTimestamp(new Date());
					}else{
						user.setMakerTimestamp(new Date());
					}	
						List<GeneralParameter> generalParameters=generalParameterService.getGeneralParameter(userRegistrationDto.getUserWrk().getLegalEntityCode(), null, null, BankAuditConstant.GP_DEFAULT_PASS,null, null, null);
						if(generalParameters!=null && !generalParameters.isEmpty()){
							userRegistrationDto.getUserWrk().setPassword(passwordEncoder.encode(generalParameters.get(0).getValue()));
							userService.updateUser(userRegistrationDto);
							serviceStatus.setStatus("success");
							serviceStatus.setMessage("successfully registered");	
						}else {
							serviceStatus.setStatus("failure");
							serviceStatus.setMessage("default password not present in db");
						}
						serviceStatus.setStatus("success");
						serviceStatus.setMessage("successfully updated");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("User is not entitled for this Role ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid user payload");
		}
		
		return serviceStatus;
	}
	

	@GetMapping(value="/isUser/{legalEntityCode}/{userId}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isUser(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("userId") String userId
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(legalEntityCode!=null 
				&& !BankAuditUtil.isEmptyString(userId)
				){
			
			try {
			    Boolean user=userService.isUser(legalEntityCode,userId);
		    	serviceStatus.setResult(user);
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully retrieved ");	
		    
				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
	

}
