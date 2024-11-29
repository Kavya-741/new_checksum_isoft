package com.bankaudit.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.LoginRequest;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.dto.UpdateTokenDetailsRequest;
import com.bankaudit.dto.UserRegistrationDto;
import com.bankaudit.model.User;

public interface UserService {

    ResponseEntity<ServiceStatus> login(String domain, LoginRequest loginRequest);

    ResponseEntity<ServiceStatus> logout(String authTokenHeader);

    ResponseEntity<ServiceStatus> updateTokenDetails(String authTokenHeader, UpdateTokenDetailsRequest updateToken);

    ResponseEntity<ServiceStatus> getUserRolesAndDetails(String authTokenHeader);

	

	DataTableResponse getUser(Integer legalEntityCode,String userId, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size);


	Boolean isUser(Integer legalEntityCode, String userId);

	Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action);

	// public void updateUser(UserRegistrationDto userRegistrationDto);

	void createUser(UserRegistrationDto userRegistrationDto);

	List<User> getUser(Integer legalEntityCode, String unitCode,String userId,String status);

	void updateUser(UserRegistrationDto userRegistrationDto);

	List<User> getUsersByLevelAndNotInGrpEntityMapping (Integer legalEntityCode, String levelCode, String status);


}
