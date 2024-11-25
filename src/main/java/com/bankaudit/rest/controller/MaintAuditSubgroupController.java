/*
 * 
 */
package com.bankaudit.rest.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.service.MaintAuditSubgroupService;

@RestController
@RequestMapping("/api/maintAuditSubgroup")
public class MaintAuditSubgroupController {

	@Autowired
	MaintAuditSubgroupService maintAuditSubgroupService;

	static final Logger logger = Logger.getLogger(MaintAuditSubgroupController.class);

	@GetMapping(value = "/getByLegalEntityCode", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditSubgroupe(@RequestParam("legalEntityCode") String legalEntityCodeStr) {
		ServiceStatus serviceStatus = new ServiceStatus();
		if (!BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
			try {
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				List<MaintAuditSubgroup> maintAuditSubgroup = maintAuditSubgroupService
						.getMaintAuditSubgroupe(legalEntityCode);
				if (maintAuditSubgroup != null && !maintAuditSubgroup.isEmpty()) {

					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved");
					serviceStatus.setResult(maintAuditSubgroup);

				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("no records found");
				}

			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("legalEntityCode required");
		}

		return serviceStatus;
	}

	@GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditSubgroupes(
			@RequestParam("legalEntityCode") String legalEntityCodeStr,
			@RequestParam(required = false, value = "auditTypeCode") String auditTypeCode,
			@RequestParam(required = false, value = "auditGroupCode") String auditGroupCode,
			@RequestParam(required = false, value = "auditSubGroupCode") String auditSubGroupCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (!BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
			try {
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				List<MaintAuditSubgroup> maintAuditSubgroups = maintAuditSubgroupService
						.getMaintAuditSubgroupes(legalEntityCode, auditTypeCode, auditGroupCode, auditSubGroupCode);
				if (maintAuditSubgroups != null && !maintAuditSubgroups.isEmpty()) {

					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved");
					serviceStatus.setResult(maintAuditSubgroups);
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("no records found");
				}

			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("legalEntityCode required");
		}

		return serviceStatus;
	}

}
