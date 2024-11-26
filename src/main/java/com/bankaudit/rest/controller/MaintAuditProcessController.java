package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
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

	@GetMapping(value = "/isMaintAuditProcess/{legalEntityCode}/{processCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditProcess(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("processCode") String processCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(processCode)) {

			try {
				Boolean maintAuditProcess = maintAuditProcessService.isMaintAuditProcess(legalEntityCode, processCode);
				serviceStatus.setResult(maintAuditProcess);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully retrieved ");

			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}

		return serviceStatus;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditProcess(@RequestBody MaintAuditProcess maintAuditProcess) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (maintAuditProcess != null && maintAuditProcess.getLegalEntityCode() != null
					&& !BankAuditUtil.isEmptyString(maintAuditProcess.getProcessCode())
					&& !BankAuditUtil.isEmptyString(maintAuditProcess.getProcessName())
					&& !BankAuditUtil.isEmptyString(maintAuditProcess.getMaker())) {

				maintAuditProcess.setProcessId(sequenceAppenderService.getAutoSequenceId());
				maintAuditProcess.setMakerTimestamp(new Date());
				maintAuditProcessService.createMaintAuditProcess(maintAuditProcess);

				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload");
			}
		} catch (Exception e) {
			serviceStatus.setStatus("failure");
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			} else
				serviceStatus.setMessage("EXCP_OCCURED");
		}

		return serviceStatus;
	}

	@GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getMaintAuditProcess(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode") String legalEntityCodeStr) {

		DataTableResponse dataTableResponse = null;

		if (!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
			try {
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				Integer draw = Integer.parseInt(drawStr);
				Integer size = Integer.parseInt(length);
				Integer page = Integer.parseInt(start) / size;
				Integer orderColumn = null;
				if (!BankAuditUtil.isEmptyString(orderColumnStr)) {
					orderColumn = Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = maintAuditProcessService.getMaintAuditProcess(legalEntityCode, search, orderColumn,
						orderDirection, page, size);
				dataTableResponse.setDraw(draw);

			} catch (Exception e) {
				dataTableResponse = new DataTableResponse();
				dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}

		} else {
			dataTableResponse = new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}

		return dataTableResponse;
	}

}
