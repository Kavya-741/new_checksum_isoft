package com.bankaudit.rest.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.MaintAuditProcess;
import com.bankaudit.service.MaintAuditProcessService;
import com.bankaudit.service.SequenceAppenderService;

@RestController
@RequestMapping("/api/maintAuditProcess")
public class MaintAuditProcessController {

	@Autowired
	MaintAuditProcessService maintAuditProcessService;


	@Autowired
	SequenceAppenderService sequenceAppenderService;

	static final Logger logger = Logger.getLogger(MaintAuditProcessController.class);

	@GetMapping(value = "/get/{legalEntityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditProcess(@PathVariable("legalEntityCode") Integer legalEntityCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null) {

			try {
				List<MaintAuditProcess> maintAuditProcesss = maintAuditProcessService
						.getMaintAuditProcess(legalEntityCode);
				if (maintAuditProcesss != null && !maintAuditProcesss.isEmpty()) {
					serviceStatus.setResult(maintAuditProcesss);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved ");
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditProcess not found");
				}

			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}

		return serviceStatus;
	}

}
