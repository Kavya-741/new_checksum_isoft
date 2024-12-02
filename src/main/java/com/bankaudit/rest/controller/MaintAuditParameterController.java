/*
 * 
 */
package com.bankaudit.rest.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService.Work;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.MaintAuditParameter;
import com.bankaudit.model.MaintAuditParameterWrk;
import com.bankaudit.service.MaintAuditParameterService;
import com.bankaudit.util.BankAuditUtil;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/maintAuditParameter")
public class MaintAuditParameterController {

	@Autowired
	MaintAuditParameterService maintAuditParameterService;

	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintAuditParameterController.class);

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditParameter(@RequestBody MaintAuditParameter maintAuditParameter) {

		ServiceStatus serviceStatus = new ServiceStatus();
		System.out.println("Inside createMaintAuditParameter ## .... ");
		try {
			if (maintAuditParameter != null && maintAuditParameter.getLegalEntityCode() > 0
					&& !BankAuditUtil.isEmptyString(maintAuditParameter.getAuditTypeCode())) {
				// maintAuditParameter.setStatus("A");
				maintAuditParameter.setMakerTimestamp(new Date());
				System.out.println("Bfr ....");
				maintAuditParameterService.createMaintAddress(maintAuditParameter);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Inside exception createMaintAuditParameter ......");
			serviceStatus.setStatus("failure");
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			} else
				serviceStatus.setMessage("EXCP_OCCURED");
		}
		return serviceStatus;
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAuditParameter(@RequestBody MaintAuditParameter maintAuditParameter) {

		ServiceStatus serviceStatus = new ServiceStatus();
		System.out.println("Inside updateMaintAuditParameter .... ");
		try {

			if (maintAuditParameter != null && maintAuditParameter.getLegalEntityCode() > 0
					&& !BankAuditUtil.isEmptyString(maintAuditParameter.getAuditTypeCode())) {
				// maintAuditParameter.setStatus("A");
				maintAuditParameter.setMakerTimestamp(new Date());
				maintAuditParameterService.updateMaintAuditParameter(maintAuditParameter);

				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure" + e.getMessage());
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		return serviceStatus;
	}

	@PostMapping(value = "/isMaintAuditParameterAlreadyExist", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditParameterAlreadyExist(@RequestBody MaintAuditParameterWrk maintAuditParameter) {
		ServiceStatus serviceStatus = new ServiceStatus();
		System.out.println("Inside updateMaintAuditParameter .... " + maintAuditParameter);
		try {
			Boolean status = null;
			boolean isExist = false;
			Map dataExist = null;

			if (maintAuditParameter != null && maintAuditParameter.getLegalEntityCode() > 0
					&& !BankAuditUtil.isEmptyString(maintAuditParameter.getAuditTypeCode())) {
				dataExist = maintAuditParameterService.isMaintAuditParameterAlreadyExist(maintAuditParameter);

				if (dataExist != null && dataExist.size() > 0) {
					status = (Boolean) dataExist.get("isExist");
					isExist = status.booleanValue();
					if (isExist) {
						serviceStatus.setStatus("success");
						serviceStatus.setMessage("Record is not exists. Please continue to save new record.");
						serviceStatus.setResult(dataExist);
					} else {
						serviceStatus.setStatus("failure");
						serviceStatus.setMessage("Record is already exists with same combinations.");
						serviceStatus.setResult(dataExist);
					}
				}

			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure" + e.getMessage());
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		return serviceStatus;
	}

	@GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getAuditParameter(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode") String legalEntityCodeStr,
			@RequestParam(required = false, value = "entitlement") String entitlement,
			@RequestParam("userName") String userName,
			@RequestParam(required = false, value = "auditTypeCode") String auditTypeCode) {

		DataTableResponse dataTableResponse = null;
		System.out.println("auditTypeCode .... " + auditTypeCode);
		if (!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)
				&& !BankAuditUtil.isEmptyString(userName)) {
			try {
				Integer legalEntityCode = Integer.parseInt(legalEntityCodeStr);
				Integer draw = Integer.parseInt(drawStr);
				Integer size = Integer.parseInt(length);
				Integer page = Integer.parseInt(start) / size;
				Integer orderColumn = null;
				if (!BankAuditUtil.isEmptyString(orderColumnStr)) {
					orderColumn = Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = maintAuditParameterService.getAuditParameter(legalEntityCode, auditTypeCode,
						entitlement, userName, search, orderColumn, orderDirection, page, size);
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

	@GetMapping(value = "/get/{legalEntityCode}/{auditTypeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditParametersByAudittype(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode) {
		ServiceStatus serviceStatus = new ServiceStatus();
		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(auditTypeCode)) {
			try {
				List<MaintAuditParameter> maintAuditParameter = maintAuditParameterService
						.getMaintAuditParameterbyAuditType(legalEntityCode, auditTypeCode);
				if (maintAuditParameter != null) {
					serviceStatus.setResult(maintAuditParameter);
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

	@GetMapping(value = "/getMaintAuditParametersPreviousRatingLov/{legalEntityCode}/{auditTypeCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditParametersPreviousRatingLov(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode) {
		ServiceStatus serviceStatus = new ServiceStatus();
		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(auditTypeCode)) {
			try {
				List<Object[]> maintAuditParameter = maintAuditParameterService
						.getMaintAuditParametersPreviousRatingLov(legalEntityCode, auditTypeCode);
				if (maintAuditParameter != null) {
					serviceStatus.setResult(maintAuditParameter);
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

	@DeleteMapping(value = "/deleteMaintAuditParameter/{legalEntityCode}/{auditTypeCode}/{id}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteEntityLevelCodeDescByLegalEntityCodeAndLevelCode(
			@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("id") Integer id,
			@PathVariable("status") String status) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(auditTypeCode)) {
			try {
				maintAuditParameterService.deleteMaintAuditParameter(legalEntityCode, auditTypeCode, id, status);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully deleted the selected record ");
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid legalEntityCode ");
		}

		return serviceStatus;
	}

	@PutMapping(value = "/maintAuditParameterList", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus validateAuditProcess(@RequestBody List<MaintAuditParameter> maintAuditParameterList) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (maintAuditParameterList != null && maintAuditParameterList.size() > 0) {
				Timestamp t = new Timestamp(System.currentTimeMillis());
				maintAuditParameterService.updateMaintAuditsParameters(maintAuditParameterList, new Date(), t);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");

				if (("A").equalsIgnoreCase(maintAuditParameterList.get(0).getStatus())) {
					boolean exeParamMatrix = maintAuditParameterService.updateMaintAuditsParametersDerived(
							maintAuditParameterList.get(0).getLegalEntityCode(),
							maintAuditParameterList.get(0).getAuditTypeCode());
					logger.info("Maint Audit Parameter Derived table Updated .. ");
				}
				// end here
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("No data tobe save...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("failure" + e.getMessage());
			if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}
		}
		return serviceStatus;
	}

}
