package com.finakon.baas.service;

import org.springframework.http.ResponseEntity;

import com.finakon.baas.dto.ApiResponse;
import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.User;

public interface UserService {

    ResponseEntity<ApiResponse> login(String domain, LoginRequest loginRequest);

    ResponseEntity<ApiResponse> logout(String authTokenHeader);

    ResponseEntity<ApiResponse> updateTokenDetails(String authTokenHeader, UpdateTokenDetailsRequest updateToken);

    ResponseEntity<ApiResponse> getUserRolesAndDetails(String authTokenHeader);

}
