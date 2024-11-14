package com.finakon.baas.controllers;

import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.LogoutRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.helper.DomainUtil;
import com.finakon.baas.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/users")
public class UserController {

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Value("${isdev}")
	private boolean isDev;

	@CrossOrigin
	@GetMapping("/healthCheck")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Service is alive");
	}

	@CrossOrigin
	@PostMapping("/login")
	public ResponseEntity<Object> login(HttpServletRequest request,
			@RequestBody LoginRequest loginRequest) {
		Map<String, Object> response = new HashMap<>();
		String domain = request.getHeader("Host");

		// if domain is null and it is not dev env return invalid domain
		if (domain == null && !isDev) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// find the legal entity code for the given domain
		MaintLegalEntity maintLegalEntity = DomainUtil.getLegalEntityCodeByDomain(domain, isDev);

		if (maintLegalEntity == null) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		return userService.login(maintLegalEntity, loginRequest);
	}

	@CrossOrigin
	@PostMapping("/updateTokenDetails")
	public ResponseEntity<Object> updateTokenDetails(HttpServletRequest request,
			@RequestBody UpdateTokenDetailsRequest updateTokenDetailsRequest) {
		Map<String, Object> response = new HashMap<>();
		String domain = request.getHeader("Host");

		// if domain is null and it is not dev env return invalid domain
		if (domain == null && !isDev) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// find the legal entity code for the given domain
		MaintLegalEntity maintLegalEntity = DomainUtil.getLegalEntityCodeByDomain(domain, isDev);

		if (maintLegalEntity == null) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		return userService.updateTokenDetails(maintLegalEntity, updateTokenDetailsRequest);
	}

	@CrossOrigin
	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request,
			@RequestBody LogoutRequest loginRequest) {
		Map<String, Object> response = new HashMap<>();
		String domain = request.getHeader("Host");

		// if domain is null and it is not dev env return invalid domain
		if (domain == null && !isDev) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// find the legal entity code for the given domain
		MaintLegalEntity maintLegalEntity = DomainUtil.getLegalEntityCodeByDomain(domain, isDev);

		if (maintLegalEntity == null) {
			response.put(Constants.SUCCESS, false);
			response.put(Constants.MESSAGE, "Invalid Domain");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		return userService.logout(maintLegalEntity, loginRequest);
	}
}
