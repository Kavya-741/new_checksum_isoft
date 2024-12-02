package com.bankaudit.rest.process.controller;

import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.process.model.IfcEnsureBaasData;
import com.bankaudit.rest.process.service.InterfaceParameterService;
import com.bankaudit.util.BankAuditUtil;

@RestController
@RequestMapping("/api/interfaceParameter")
public class InterfaceParameterController {

	@Autowired
	InterfaceParameterService interfaceParameterService;

	static final Logger logger = Logger.getLogger(InterfaceParameterController.class);

	@GetMapping(value = "/getLatestParametersByAuditId/{legalEntityCode}/{auditId}/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getLatestParametersByAuditId(
			@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditId") String auditId,
			@PathVariable("unitId") String unitId) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null && !BankAuditUtil.isEmptyString(auditId)) {
			try {
				List<IfcEnsureBaasData> ifcBaasDataLst = interfaceParameterService
						.getLatestParametersByAuditId(legalEntityCode, auditId, unitId);
				if (ifcBaasDataLst != null) {
					serviceStatus.setResult(ifcBaasDataLst);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved ");
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("ids not found");
				}

			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid parameter");
		}
		return serviceStatus;
	}
}
