/*
 * 
 */
package com.finakon.baas.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.service.ServiceInterfaces.GeneralParameterService;

/**
 * The Class {@link GeneralParameterController} provides the REST services to
 * manage the {@link GeneralParameter} class objects .
 *
 * @author amit.patel
 * @version 1.0
 */
@RestController
@RequestMapping("/generalParameter")
public class GeneralParameterController {

	/**
	 * The general parameter service is autowired and make methods available
	 * from service layer .
	 */
	@Autowired
	GeneralParameterService generalParameterService;

	/**
	 * This method is use to Gets the general parameter.
	 *
	 * @param legalEntityCodeStr
	 *                           specify the legal entity code str
	 * @param modCode
	 *                           specify the mod code
	 * @param language
	 *                           specify the language
	 * @param key1
	 *                           specify the key 1
	 * @param key2
	 *                           specify the key 2
	 * @param value
	 *                           specify the value
	 * @param maker
	 *                           specify the maker
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse getGeneralParameter(
			@RequestParam(value = "legalEntityCode", required = false) String legalEntityCodeStr,
			@RequestParam(value = "modCode", required = false) String modCode,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "key1", required = false) String key1,
			@RequestParam(value = "key2", required = false) String key2,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "maker", required = false) String maker) {

		ApiResponse apiResponse = new ApiResponse();
		try {
			Integer legalEntityCode = null;
			if (!BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
				legalEntityCode = Integer.parseInt(legalEntityCodeStr);
			} else {
				legalEntityCode = 0;
			}

			List<GeneralParameter> generalParameters = null;

			generalParameters = generalParameterService.getGeneralParameter(legalEntityCode, modCode, language, key1,
					key2, value, maker);

			if (generalParameters != null && !generalParameters.isEmpty()) {
				apiResponse.setResult(generalParameters);
				apiResponse.setStatus(Constants.SUCCESS);
				apiResponse.setMessage("successfully retrieved");
			} else {
				apiResponse.setStatus(Constants.FAILURE);
				apiResponse.setMessage("no records found");

			}

		} catch (Exception e) {
			e.printStackTrace();
			apiResponse.setStatus(Constants.FAILURE);
			apiResponse.setMessage("failure");

		}

		return apiResponse;
	}
}
