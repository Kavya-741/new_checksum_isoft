/*
 * 
 */
package com.bankaudit.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.helper.Constants;
import com.bankaudit.model.GeneralParameter;
import com.bankaudit.service.GeneralParameterService;

/**
 * The Class {@link GeneralParameterController} provides the REST services to
 * manage the {@link GeneralParameter} class objects .
 *
 * @author amit.patel
 * @version 1.0
 */
@RestController
@RequestMapping("/api/generalParameter")
public class GeneralParameterController {
	@Autowired
	GeneralParameterService generalParameterService;

	@GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getGeneralParameter(
			@RequestParam(value = "legalEntityCode", required = false) String legalEntityCodeStr,
			@RequestParam(value = "modCode", required = false) String modCode,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "key1", required = false) String key1,
			@RequestParam(value = "key2", required = false) String key2,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "maker", required = false) String maker) {

		ServiceStatus apiResponse = new ServiceStatus();
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
