package com.bankaudit.rest.process.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bankaudit.process.model.AuditLevelDocumentDetails;
import com.bankaudit.rest.process.service.IfcEnsureBaasDataService;
import com.bankaudit.dto.ServiceStatus;

@RestController
@RequestMapping("/api/ifcEnsureBaasData")
public class IfcEnsureBaasDataController {

	@Autowired
	IfcEnsureBaasDataService ifcEnsureBaasDataService;

	static final Logger logger = Logger.getLogger(IfcEnsureBaasDataController.class);

	@GetMapping(value = "/getAuditLevelDocuments", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getAuditLevelDocuments(
			@RequestParam("legalEntityCode") Integer legalEntityCode,
			@RequestParam("auditId") String auditId,
			@RequestParam("planId") String planId,
			@RequestParam("auditTypeCode") String auditTypeCode,
			@RequestParam(value = "documentType", required = false) String documentType) {

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

			List<AuditLevelDocumentDetails> auditLevelDocumentDetails = ifcEnsureBaasDataService
					.getAuditLevelDocuments(legalEntityCode, auditTypeCode, auditId, planId, documentType);

			if (auditLevelDocumentDetails != null && !auditLevelDocumentDetails.isEmpty()) {
				serviceStatus.setResult(auditLevelDocumentDetails);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("File successfully deleted  ");

			} else {
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("No docs found ");
			}

		} catch (Exception e) {
			logger.error(e.getStackTrace());
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure");
		}
		return serviceStatus;
	}
}
