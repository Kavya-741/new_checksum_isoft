/*
 * 
 */
package com.bankaudit.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.service.MaintAuditSubgroupService;

/**
 * The Class {@link MaintAuditSubgroupController} provides the REST services to
 * manage the {@link MaintAuditSubgroup} class objects .
 *
 * @author amit.patel
 * @version 1.0
 */
@RestController
@RequestMapping("/maintAuditSubgroup")
public class MaintAuditSubgroupController {

	/**
	 * The maint audit subgroup service is autowired and make methods available
	 * from service layer .
	 */
	@Autowired
	MaintAuditSubgroupService maintAuditSubgroupService;

	/** The Constant logger is used to specify the . */
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

}
