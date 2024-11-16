package com.finakon.baas.controllers;

import com.finakon.baas.dto.ApiResponse;
import com.finakon.baas.dto.GetTokenDetailsDTO;
import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.LogoutRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.User;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.helper.DomainUtil;
import com.finakon.baas.jwthelper.JwtTokenUtil;
import com.finakon.baas.service.UserService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@CrossOrigin
	@GetMapping("/healthCheck")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Service is alive");
	}

	@CrossOrigin
	@PostMapping("/login")
	public ResponseEntity<ApiResponse> login(HttpServletRequest request,
			@RequestBody LoginRequest loginRequest) {
		String domain = request.getHeader("Host");
		return userService.login(domain, loginRequest);
	}

	@CrossOrigin
	@PostMapping("/updateTokenDetails")
	public ResponseEntity<ApiResponse> updateTokenDetails(HttpServletRequest request,
			@RequestBody UpdateTokenDetailsRequest updateTokenDetailsRequest) {
				String authorizationHeader = request.getHeader("Authorization");
		return userService.updateTokenDetails(authorizationHeader, updateTokenDetailsRequest);
	}

	@CrossOrigin
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		return userService.logout(authorizationHeader);
	}

	@PostMapping(value = "/getUserRolesAndDetails", produces = { "application/json" }, consumes = {
			"application/json" })
	public ResponseEntity<ApiResponse> getUserRolesAndDetails(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		return userService.getUserRolesAndDetails(authorizationHeader);
	}
}
