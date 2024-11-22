package com.finakon.baas.controllers;

import com.finakon.baas.dto.Response.ApiResponse;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.service.ServiceInterfaces.MaintUsergroupRolesService;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintUsergroupRoles")
public class MaintUsergroupRolesController {

	/**
	 * The maint usergroup roles service is autowired and make methods available
	 * from service layer .
	 */
	@Autowired
	MaintUsergroupRolesService maintUsergroupRolesService;


	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintUsergroupRolesController.class);

	/**
	 * This method is use to Creates the maint usergroup roles.
	 *
	 * @param maintUsergroupRolesDto
	 *                               specify the maint usergroup roles dto
	 * @return the service status class object with response status and payload
	 *         .
	 */

	@GetMapping(value = "/getByLegalEntityCode/{legalEntityCode}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	ApiResponse getMaintUsergroupRolesByLegalEntityCode(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("status") String status) {
				ApiResponse apiResponse = new ApiResponse();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(status)) {

			try {
				List<?> maintUsergroupRoles = maintUsergroupRolesService
						.getMaintUsergroupRolesByLegalEntityCodeAndUserId(legalEntityCode, status);
				if (maintUsergroupRoles != null && !maintUsergroupRoles.isEmpty()) {
					apiResponse.setResult(maintUsergroupRoles);
					apiResponse.setStatus("success");
					apiResponse.setMessage("successfully retrieved ");
				} else {
					apiResponse.setStatus("failure");
					apiResponse.setMessage("ugRole not found");
				}

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

}
