package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditSubgroup;
import com.bankaudit.service.MaintAuditSubgroupService;

@CrossOrigin("*")
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

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditSubgroup(@RequestBody MaintAuditSubgroup maintAuditSubgroup) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (maintAuditSubgroup != null && maintAuditSubgroup.getLegalEntityCode() != null
					&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditSubGroupCode())
					&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditSubGroupName())
					&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditTypeCode())
					&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditGroupCode())
					&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getMaker())) {
				if (maintAuditSubgroup.getStatus().equals(BankAuditConstant.STATUS_AUTH)) {
					maintAuditSubgroup.setCheckerTimestamp(new Date());
				} else {
					maintAuditSubgroup.setMakerTimestamp(new Date());
				}

				maintAuditSubgroupService.createMaintAuditSubgroup(maintAuditSubgroup);

				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
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

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAuditSubgroup(@RequestBody MaintAuditSubgroup maintAuditSubgroup) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (maintAuditSubgroup != null && maintAuditSubgroup.getLegalEntityCode() != null
				&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditSubGroupName())
				&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditTypeCode())
				&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditGroupCode())
				&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getAuditSubGroupCode())
				&& !BankAuditUtil.isEmptyString(maintAuditSubgroup.getMaker())) {
			try {
				maintAuditSubgroup.setMakerTimestamp(new Date());
				maintAuditSubgroupService.updateMaintAuditSubgroup(maintAuditSubgroup);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully updated");
			} catch (Exception e) {

				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}

		return serviceStatus;
	}

	@GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getMaintAuditSubgroupes(
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
				dataTableResponse = maintAuditSubgroupService.getMaintAuditSubgroupes(legalEntityCode, search,
						orderColumn, orderDirection, page, size);
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

	@GetMapping(value = "/isMaintAuditSubgroup/{legalEntityCode}/{auditTypeCode}/{auditGroupCode}/{auditSubGroupCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditSubgroup(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("auditGroupCode") String auditGroupCode,
			@PathVariable("auditSubGroupCode") String auditSubGroupCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(auditTypeCode)
				&& !BankAuditUtil.isEmptyString(auditGroupCode)
				&& !BankAuditUtil.isEmptyString(auditSubGroupCode)) {
			try {
				Boolean maintAuditSubgroup = maintAuditSubgroupService.isMaintAuditSubgroup(legalEntityCode,
						auditTypeCode, auditGroupCode, auditSubGroupCode);
				serviceStatus.setResult(maintAuditSubgroup);
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

	@GetMapping(value = "/isMaintAuditSubgroupLE/{legalEntityCode}/{auditSubGroupCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditSubgroupLE(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditSubGroupCode") String auditSubGroupCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null && !BankAuditUtil.isEmptyString(auditSubGroupCode)) {
			try {
				Boolean maintAuditSubgroup = maintAuditSubgroupService.isMaintAuditSubgroupLE(legalEntityCode,
						auditSubGroupCode);
				serviceStatus.setResult(maintAuditSubgroup);
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

	@DeleteMapping(value = "/deleteAuditSubGroupByID/{legalEntityCode}/{auditTypeCode}/{auditGroupCode}/{auditSubGroupCode}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteAuditSubGroupByID(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode, @PathVariable("auditGroupCode") String auditGroupCode,
			@PathVariable("auditSubGroupCode") String auditSubGroupCode, @PathVariable("userId") String userId) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null && !BankAuditUtil.isEmptyString(auditTypeCode)
				&& !BankAuditUtil.isEmptyString(auditGroupCode)) {
			try {
				maintAuditSubgroupService.deleteAuditSubGroupByID(legalEntityCode, auditTypeCode, auditGroupCode,
						auditSubGroupCode, userId);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("Record deleted successfully.");
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				e.printStackTrace();
				if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
				serviceStatus.setMessage("failure");
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}

	@GetMapping(value = "/get/{legalEntityCode}/{auditSubGroupCode}/{auditGroupCode}/{auditTypeCode}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditSubgroup(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditSubGroupCode") String auditSubGroupCode,
			@PathVariable("auditGroupCode") String auditGroupCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("status") String status
			) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(auditSubGroupCode)
				&& !BankAuditUtil.isEmptyString(auditGroupCode)
				&& !BankAuditUtil.isEmptyString(auditTypeCode)
				) {

			try {
				MaintAuditSubgroup maintAuditSubgroup = maintAuditSubgroupService.getMaintAuditSubgroup(legalEntityCode,
						auditSubGroupCode,auditGroupCode,auditTypeCode,status);
				if (maintAuditSubgroup != null) {
					serviceStatus.setResult(maintAuditSubgroup);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved ");
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("MaintAuditSubgroup not found");
				}

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

}
