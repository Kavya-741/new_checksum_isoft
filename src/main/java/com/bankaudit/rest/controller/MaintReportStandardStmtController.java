package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintReportStandardStmt;
import com.bankaudit.service.MaintReportStandardStmtService;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.dto.ServiceStatus;

@RestController
@RequestMapping("/api/maintReportStandardStmt")
public class MaintReportStandardStmtController {

	@Autowired
	MaintReportStandardStmtService maintReportStandardStmtService;

	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintReportStandardStmtController.class);

	@PostMapping
	public ServiceStatus createMaintReportStandardStmt(@RequestBody MaintReportStandardStmt maintReportStandardStmt) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (maintReportStandardStmt != null && maintReportStandardStmt.getLegalEntityCode() != null
				&& !BankAuditUtil.isEmptyString(maintReportStandardStmt.getMappingId())
				&& !BankAuditUtil.isEmptyString(maintReportStandardStmt.getStatus())) {
			try {
				maintReportStandardStmt.setMakerTimestamp(new Date());
				maintReportStandardStmtService.createMaintReportStandardStmt(maintReportStandardStmt);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage(" successfully updated ");
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage(e.getMessage());
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}

	@PutMapping
	public ServiceStatus updateMaintReportStandardStmt(@RequestBody MaintReportStandardStmt maintReportStandardStmt) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (maintReportStandardStmt != null && maintReportStandardStmt.getLegalEntityCode() != null
				&& !BankAuditUtil.isEmptyString(maintReportStandardStmt.getMappingId())
				&& !BankAuditUtil.isEmptyString(maintReportStandardStmt.getStatus())) {

			try {
				logger.info("inside updateMaintReportStandardStmt ....");
				maintReportStandardStmt.setMakerTimestamp(new Date());
				maintReportStandardStmtService.updateMaintReportStandardStmt(maintReportStandardStmt);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage(" successfully updated ");
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage(e.getMessage());
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}

	@GetMapping(value = "/getAll")
	public DataTableResponse getAllMaintReportStandardStmt(@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start, @RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode") String legalEntityCodeStr) {

		DataTableResponse dataTableResponse = null;

		if (!BankAuditUtil.isEmptyString(drawStr) && !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)) {
			try {
				Integer draw = Integer.parseInt(drawStr);
				Integer size = Integer.parseInt(length);
				Integer page = Integer.parseInt(start) / size;
				Integer orderColumn = null;
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				if (!BankAuditUtil.isEmptyString(orderColumnStr)) {
					orderColumn = Integer.parseInt(orderColumnStr);
				}

				dataTableResponse = maintReportStandardStmtService.getAllMaintReportStandardStmt(legalEntityCode,
						search, orderColumn, orderDirection,
						page, size);
				dataTableResponse.setDraw(draw);

			} catch (Exception e) {
				dataTableResponse = new DataTableResponse();
				dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}

		} else {
			dataTableResponse = new DataTableResponse();
			dataTableResponse.setError("invalid payload");
		}

		return dataTableResponse;
	}

	@DeleteMapping
	public ServiceStatus deleteMaintReportStandardStmt(@RequestParam("legalEntityCode") Integer legalEntityCode,
			@RequestParam("mappingId") String mappingId, @RequestParam("status") String status) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null && !BankAuditUtil.isEmptyString(mappingId)
				&& !BankAuditUtil.isEmptyString(status)) {

			try {
				logger.info("inside deleteMaintReportStandardStmt ....");
				maintReportStandardStmtService.deleteMaintReportStandardStmt(legalEntityCode, mappingId, status);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage(" successfully updated ");
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage(e.getMessage());
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}

	@GetMapping(value = "/getMaintReportStandardStmt")
	public ServiceStatus getMaintReportStandardStmt(@RequestParam("legalEntityCode") Integer legalEntityCode,
			@RequestParam("mappingId") String mappingId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		if (legalEntityCode != null && mappingId != null) {
			MaintReportStandardStmt wrk = maintReportStandardStmtService.getMaintReportStandardStmtWrk(legalEntityCode,
					mappingId);
			if (wrk != null) {
				serviceStatus.setResult(wrk);
			} else {
				serviceStatus.setResult(
						maintReportStandardStmtService.getMaintReportStandardStmt(legalEntityCode, mappingId));
			}
			serviceStatus.setStatus("success");
			if (serviceStatus.getResult() != null)
				serviceStatus.setMessage("Standard Statement already exists, do you want to modify");
			else {
				serviceStatus.setMessage("No standard Statement found");
				serviceStatus.setStatus("failure");
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}

		return serviceStatus;
	}

}
