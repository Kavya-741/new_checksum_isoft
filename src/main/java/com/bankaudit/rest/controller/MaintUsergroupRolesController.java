package com.bankaudit.rest.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.service.MaintUsergroupRolesService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/maintUsergroupRoles")
public class MaintUsergroupRolesController {

	@Autowired
	MaintUsergroupRolesService maintUsergroupRolesService;

	static final Logger logger = Logger.getLogger(MaintUsergroupRolesController.class);

	@GetMapping(value = "/getByLegalEntityCode/{legalEntityCode}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintUsergroupRolesByLegalEntityCode(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("status") String status) {
				ServiceStatus apiResponse = new ServiceStatus();

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
