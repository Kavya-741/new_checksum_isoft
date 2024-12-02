
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.ActivityProcessFindingMapping;
import com.bankaudit.service.ActivityProcessFindingMappingService;
import com.bankaudit.service.SequenceAppenderService;
import com.bankaudit.helper.BankAuditUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/activityProcessFindingMapping")
public class ActivityProcessFindingMappingController {

	@Autowired
	ActivityProcessFindingMappingService activityProcessFindingMappingService;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	static final Logger logger = Logger.getLogger(ActivityProcessFindingMappingController.class);

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createActivityProcessFindingMapping(
			@RequestBody ActivityProcessFindingMapping activityProcessFindingMapping) {
		ServiceStatus serviceStatus = new ServiceStatus();

		try {
			if (activityProcessFindingMapping != null
					&& activityProcessFindingMapping.getLegalEntityCode() != null
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditGroupCode())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditSubGroupCode())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditTypeCode())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getMaker())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getFindingId())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getProcessId())
					&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getActivityId())) {

				activityProcessFindingMapping.setMappingId(sequenceAppenderService.getAutoSequenceId());
				activityProcessFindingMapping.setMappingCode(sequenceAppenderService.getAutoSequenceId());

				activityProcessFindingMapping.setMakerTimestamp(new Date());

				activityProcessFindingMappingService.createActivityProcessFindingMapping(activityProcessFindingMapping);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
				serviceStatus.setResult(activityProcessFindingMapping.getMappingId());

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
	DataTableResponse getActivityProcessFindingMappings(
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
				dataTableResponse = activityProcessFindingMappingService.getActivityProcessFindingMappings(
						legalEntityCode, search, orderColumn, orderDirection, page, size);
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

	@GetMapping(value = "/isActivityProcessFindingMapping", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditFinding(
			@RequestParam(required = true, value = "legalEntityCode") String legalEntityCode,
			@RequestParam(required = true, value = "auditTypeCode") String auditTypeCode,
			@RequestParam(required = true, value = "auditGroupCode") String auditGroupCode,
			@RequestParam(required = true, value = "auditSubGroupCode") String auditSubGroupCode,
			@RequestParam(required = true, value = "activityCode") String activityCode,
			@RequestParam(required = true, value = "processCode") String processCode,
			@RequestParam(required = true, value = "findingCode") String findingCode,
			@RequestParam(required = false, value = "mappingId") String mappingId,
			@RequestParam(required = false, value = "riskBelongsTo") String riskBelongsTo) {
		ServiceStatus serviceStatus = new ServiceStatus();
		if (legalEntityCode != null) {

			try {
				Boolean activityProcessFindingMapping = activityProcessFindingMappingService
						.isActivityProcessFindingMapping(Integer.parseInt(legalEntityCode), auditTypeCode,
								auditGroupCode, auditSubGroupCode, activityCode, processCode, findingCode, mappingId,
								riskBelongsTo); // riskBelongsTo added to handle one more parameter in RCSA Setup
				serviceStatus.setResult(activityProcessFindingMapping);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully retrieved");

			} catch (Exception e) {
				/* e.printStackTrace(); */
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}

		} else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}

		return serviceStatus;
	}

	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateActivityProcessFindingMapping(
			@RequestBody ActivityProcessFindingMapping activityProcessFindingMapping) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (activityProcessFindingMapping != null
				&& activityProcessFindingMapping.getLegalEntityCode() != null
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getActivityId())
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditGroupCode())
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditSubGroupCode())
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getAuditTypeCode())
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getFindingId())
				&& activityProcessFindingMapping.getMappingId() != null
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getProcessId())
				&& !BankAuditUtil.isEmptyString(activityProcessFindingMapping.getMaker())) {
			try {
				if (activityProcessFindingMapping.getStatus().equals(BankAuditConstant.STATUS_AUTH)) {
					activityProcessFindingMapping.setCheckerTimestamp(new Date());
				} else {
					activityProcessFindingMapping.setMakerTimestamp(new Date());
				}
				activityProcessFindingMappingService.updateActivityProcessFindingMapping(activityProcessFindingMapping);

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

	@DeleteMapping(value="/deleteActivityProcessFindingMapping/{legalEntityCode}/{mappingId}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteActivityProcessFindingMapping(
			@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("mappingId")String mappingId,
			@PathVariable("status")String status){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				&& !BankAuditUtil.isEmptyString(mappingId)){
			try {				
				activityProcessFindingMappingService.deleteActivityProcessFindingMapping(legalEntityCode, mappingId, status);
				serviceStatus.setStatus("success"); 
				serviceStatus.setMessage("successfully deleted the rejected record ");				
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid legalEntityCode ");
		}
		
		return serviceStatus;
	}

	@GetMapping(value="/getDynamicValues",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getActivityProcessFindingMappingValues(
		/*	@PathVariable("createOrUpdate")String createOrUpdate,*/
			@RequestParam(required=true,value="legalEntityCode")String legalEntityCode,
			@RequestParam(required=true,value="auditTypeCode") String auditTypeCode,
			@RequestParam(required=false,value="auditGroupCode") String auditGroupCode,
			@RequestParam(required=false,value="auditSubGroupCode") String auditSubGroupCode,
			@RequestParam(required=false,value="activityId") String activityId,
			@RequestParam(required=false,value="processId") String processId,
			@RequestParam(required=false,value="findingId") String findingId
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(!BankAuditUtil.isEmptyString(auditTypeCode)
				&& !BankAuditUtil.isEmptyString(legalEntityCode)){
			try {
				List<String>  values = activityProcessFindingMappingService
						.getActivityProcessFindingMappingValues(legalEntityCode , auditTypeCode , auditGroupCode , 
								auditSubGroupCode ,activityId, processId,findingId,"C");
			    if(values!=null && !values.isEmpty()){
			    	serviceStatus.setResult(values);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage(" No records found ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
}
