package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.finakon.baas.dto.Request.LoginRequest;
import com.finakon.baas.dto.Request.UpdateTokenDetailsRequest;
import com.finakon.baas.dto.Request.UserRegistrationDto;
import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.User;

public interface UserService {

    ResponseEntity<ApiResponse> login(String domain, LoginRequest loginRequest);

    ResponseEntity<ApiResponse> logout(String authTokenHeader);

    ResponseEntity<ApiResponse> updateTokenDetails(String authTokenHeader, UpdateTokenDetailsRequest updateToken);

    ResponseEntity<ApiResponse> getUserRolesAndDetails(String authTokenHeader);

    	/**
	 * This method is use to Gets the user.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @param userId
	 *            specify the user id
	 * @param search
	 *            specify the search
	 * @param orderColumn
	 *            specify the order column
	 * @param orderDirection
	 *            specify the order direction
	 * @param page
	 *            specify the page
	 * @param size
	 *            specify the size
	 * @return the data table response .
	 */
	DataTableResponse getUser(Integer legalEntityCode,String userId, String search, Integer orderColumn, String orderDirection,
			Integer page, Integer size);


	Boolean isUser(Integer legalEntityCode, String userId);

	Boolean isUserHavingEntitlement(Integer legalEntityCode, String userId, String role, String functionId, String action);

	// public void updateUser(UserRegistrationDto userRegistrationDto);

	public void createUser(UserRegistrationDto userRegistrationDto);

	public List<User> getUser(Integer legalEntityCode, String unitCode,String userId,String status);

	public void updateUser(UserRegistrationDto userRegistrationDto);

}
