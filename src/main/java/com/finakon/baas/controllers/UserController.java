package com.finakon.baas.controllers;

import com.finakon.baas.dto.Request.LoginRequest;
import com.finakon.baas.dto.Request.UpdateTokenDetailsRequest;
import com.finakon.baas.dto.Request.UserRegistrationDto;
import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.UserWrk;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.repository.JPARepositories.MaintLegalEntityRepository;
import com.finakon.baas.service.ServiceInterfaces.GeneralParameterService;
import com.finakon.baas.service.ServiceInterfaces.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private MaintLegalEntityRepository maintLegalEntityRepository;

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
	public ResponseEntity<ApiResponse> login(HttpServletRequest request,
			@RequestBody LoginRequest loginRequest) {
		String domain = request.getHeader("Host");
		return userService.login(domain, loginRequest);
	}

	@CrossOrigin
	@PostMapping(value = "/updateTokenDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse> updateTokenDetails(HttpServletRequest request,
			@RequestBody UpdateTokenDetailsRequest updateTokenDetailsRequest) {
		String authorizationHeader = request.getHeader("Authorization");
		return userService.updateTokenDetails(authorizationHeader, updateTokenDetailsRequest);
	}

	@CrossOrigin
	@PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		return userService.logout(authorizationHeader);
	}

	@CrossOrigin
	@PostMapping(value = "/getUserRolesAndDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse> getUserRolesAndDetails(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		return userService.getUserRolesAndDetails(authorizationHeader);
	}

	@CrossOrigin
	@GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getUser(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode") String legalEntityCodeStr,
			@RequestParam("userId") String userId) {

		DataTableResponse dataTableResponse = null;

		if (!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
			try {
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				Integer draw = Integer.parseInt(drawStr);
				Integer size = Integer.parseInt(length);
				Integer page = Integer.parseInt(start) / size;
				Integer orderColumn = null;
				if (!BankAuditUtil.isEmptyString(orderColumnStr)) {
					orderColumn = Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = userService.getUser(legalEntityCode, userId, search, orderColumn, orderDirection,
						page, size);
				dataTableResponse.setDraw(draw);

			} catch (Exception e) {
				dataTableResponse = new DataTableResponse();
				dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}

		} else {
			dataTableResponse = new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}
		return dataTableResponse;
	}

	@GetMapping(value = "/isUser/{legalEntityCode}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse isUser(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("userId") String userId) {
		ApiResponse apiResponse = new ApiResponse();
		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(userId)) {

			try {
				Boolean user = userService.isUser(legalEntityCode, userId);
				apiResponse.setResult(user);
				apiResponse.setStatus("success");
				apiResponse.setMessage("successfully retrieved ");

			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
			}

		} else {
			apiResponse.setStatus("failure");
			apiResponse.setMessage("invalid payload ");
		}

		return apiResponse;
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse updateUser(@RequestBody UserRegistrationDto userRegistrationDto,
			@RequestParam(required = false, value = "legalEntityCode") String legalEntityCodeStr,
			@RequestParam(required = false, value = "userCode") String userId,
			@RequestParam(required = false, value = "role") String role) {

		ApiResponse apiResponse = new ApiResponse();
		logger.info("updateUser .. " + userRegistrationDto.getUserWrk().getMobile1() + " M2.. "
				+ userRegistrationDto.getUserWrk().getMobile2() + " L1.. "
				+ userRegistrationDto.getUserWrk().getLandline1());
		if (userRegistrationDto != null
				&& userRegistrationDto.getUserWrk() != null
				&& userRegistrationDto.getUserWrk().getLegalEntityCode() != null
				&& !BankAuditUtil.isEmptyString(userRegistrationDto.getUserWrk().getUserId())
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile1())
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile2())
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getLandline1())) {
			try {
				boolean validUsrforRole = userService.isUserHavingEntitlement(Integer.parseInt(legalEntityCodeStr),
						userId, role, functionId, "UPDATE"); // To validate if the User having the particular role for
																// VAPT (Broken access control)
				if (validUsrforRole) {

					UserWrk user = userRegistrationDto.getUserWrk();
					MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(user.getLegalEntityCode());
					if (userRegistrationDto.getUserWrk().getEntityStatus().equalsIgnoreCase("A"))
						userRegistrationDto.getUserWrk().setUnsuccessfulAttempts(0);
					if (user.getStatus().equals(BankAuditConstant.STATUS_AUTH)) {
						user.setCheckerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
					} else {
						user.setMakerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
					}
					List<GeneralParameter> generalParameters = generalParameterService.getGeneralParameter(
							userRegistrationDto.getUserWrk().getLegalEntityCode(), null, null,
							BankAuditConstant.GP_DEFAULT_PASS, null, null, null);
					if (generalParameters != null && !generalParameters.isEmpty()) {
						userRegistrationDto.getUserWrk()
								.setPassword(passwordEncoder.encode(generalParameters.get(0).getValue()));
						userService.updateUser(userRegistrationDto);
						apiResponse.setStatus("success");
						apiResponse.setMessage("successfully registered");
					} else {
						apiResponse.setStatus("failure");
						apiResponse.setMessage("default password not present in db");
					}
					apiResponse.setStatus("success");
					apiResponse.setMessage("successfully updated");
				} else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("User is not entitled for this Role ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				apiResponse.setStatus("failure");
				apiResponse.setMessage("failure");
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					apiResponse.setMessage("DATAINTGRTY_VIOLATION");
				}
			}

		} else {
			apiResponse.setStatus("failure");
			apiResponse.setMessage("invalid user payload");
		}

		return apiResponse;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse createUser(@RequestBody UserRegistrationDto userRegistrationDto,
			@RequestParam(required = false, value = "legalEntityCode") String legalEntityCodeStr,
			@RequestParam(required = false, value = "userCode") String userId,
			@RequestParam(required = false, value = "role") String role) {

		ApiResponse serviceStatus = new ApiResponse();

		if (userRegistrationDto != null
				&& userRegistrationDto.getUserWrk() != null
				&& userRegistrationDto.getUserWrk().getLegalEntityCode() != null
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile1())
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getMobile2())
				&& BankAuditUtil.isNumber(userRegistrationDto.getUserWrk().getLandline1())
		// all validations required
		) {
			try {
				logger.info("Inside User createUser() ..");
				boolean validUsrforRole = userService.isUserHavingEntitlement(Integer.parseInt(legalEntityCodeStr),
						userId, role, functionId, "CREATE"); // To validate if the User having the particular role for
																// VAPT (Broken access control)
				MaintLegalEntity maintLegalEntity = maintLegalEntityRepository.findByLegalEntityCode(Integer.parseInt(legalEntityCodeStr));

				if (validUsrforRole) {
					userRegistrationDto.getUserWrk()
							.setMakerTimestamp(maintLegalEntity.getBusinessdateTimestamp());
					List<GeneralParameter> generalParameters = generalParameterService.getGeneralParameter(
							userRegistrationDto.getUserWrk().getLegalEntityCode(), null, null,
							BankAuditConstant.GP_DEFAULT_PASS, null, null, null);
					if (generalParameters != null && !generalParameters.isEmpty()) {
						userRegistrationDto.getUserWrk()
								.setPassword(passwordEncoder.encode(generalParameters.get(0).getValue()));
						userService.createUser(userRegistrationDto);
						serviceStatus.setStatus("success");
						serviceStatus.setMessage("successfully registered");
					} else {
						serviceStatus.setStatus("failure");
						serviceStatus.setMessage("default password not present in db");
					}
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("User is not entitled for this Role ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid user payload");
		}
		return serviceStatus;
	}

}
