package com.finakon.baas.controllers;

import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.entities.MaintEntity;
import com.finakon.baas.helper.Constants;
import com.finakon.baas.service.ServiceInterfaces.MaintEntityService;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintEntity")
public class MaintEntityController {

	@Autowired
	MaintEntityService maintEntityService;

	static final Logger logger = Logger.getLogger(MaintEntityController.class);

	@GetMapping(value = "/getByLegalEntityCode/{legalEntityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse getMaintEntityByLegalEntityCode(
			@PathVariable("legalEntityCode") Integer legalEntityCode) {
		ApiResponse apiResponse = new ApiResponse();

		if (legalEntityCode != null) {
			try {
				List<MaintEntity> maintEntities = maintEntityService.getMaintEntityByLegalEntityCode(legalEntityCode);

				if (maintEntities != null && !maintEntities.isEmpty()) {
					apiResponse.setResult(maintEntities);
					apiResponse.setStatus(Constants.SUCCESS);
					apiResponse.setMessage(Constants.UserControllerErrorCode.SUCCESSFUL_RETRIEVED);
				} else {
					apiResponse.setStatus(Constants.FAILURE);
					apiResponse.setMessage(Constants.UserControllerErrorCode.NOT_FOUND);
				}

			} catch (Exception e) {
				apiResponse.setStatus(Constants.FAILURE);
				apiResponse.setMessage(Constants.FAILURE);
				e.printStackTrace();
			}

		} else {
			apiResponse.setStatus(Constants.FAILURE);
			apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_PARAMETER);
		}
		return apiResponse;
	}
}
