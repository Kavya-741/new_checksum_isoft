package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.bankaudit.model.MaintAuditActivity;
import com.bankaudit.service.MaintAuditActivityService;
import com.bankaudit.service.SequenceAppenderService;
import com.bankaudit.util.BankAuditUtil;

@RestController
@RequestMapping("/api/maintAuditActivity")
public class MaintAuditActivityController {

	@Autowired
	MaintAuditActivityService maintAuditActivityService;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	static final Logger logger = Logger.getLogger(MaintAuditActivityController.class);

	@GetMapping(value = "/get/{legalEntityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditActivity(@PathVariable("legalEntityCode") Integer legalEntityCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null) {

			try {
				List<MaintAuditActivity> maintAuditActivities = maintAuditActivityService
						.getMaintAuditActivity(legalEntityCode);
				if (maintAuditActivities != null && !maintAuditActivities.isEmpty()) {
					serviceStatus.setResult(maintAuditActivities);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved ");
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditActivity not found");
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

	@GetMapping(value = "/isMaintAuditActivity/{legalEntityCode}/{activityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditActivity(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("activityCode") String activityCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(activityCode)) {

			try {
				Boolean maintAuditActivity = maintAuditActivityService.isMaintAuditActivity(legalEntityCode,
						activityCode);
				serviceStatus.setResult(maintAuditActivity);
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

	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditActivity(@RequestBody MaintAuditActivity maintAuditActivity){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		try {
			if(maintAuditActivity!=null
					&& maintAuditActivity.getLegalEntityCode()!=null
					&& !BankAuditUtil.isEmptyString(maintAuditActivity.getActivityCode())
					&& !BankAuditUtil.isEmptyString(maintAuditActivity.getActivityName())
					&& !BankAuditUtil.isEmptyString(maintAuditActivity.getMaker())
					){ 
				    maintAuditActivity.setActivityId(sequenceAppenderService.getAutoSequenceId());
				    maintAuditActivity.setMakerTimestamp(new Date());
				    
				    maintAuditActivityService.createMaintAuditActivity(maintAuditActivity);
				    
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully created");
			}else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload ");
			}
		}catch (Exception e) {
			serviceStatus.setStatus("failure");
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}else serviceStatus.setMessage("EXCP_OCCURED");
		}
		
		return serviceStatus;
	}

	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getMaintAuditActivities(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr
			){
		
		DataTableResponse dataTableResponse=null;
		
		if(!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				Integer draw=Integer.parseInt(drawStr);
				Integer size=Integer.parseInt(length);
				Integer page=Integer.parseInt(start)/size;
				Integer orderColumn=null;
				if(!BankAuditUtil.isEmptyString(orderColumnStr)){
					orderColumn=Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = maintAuditActivityService.getMaintAuditActivities(legalEntityCode,search,orderColumn,orderDirection,page,size);
				dataTableResponse.setDraw(draw);    
					
			} catch (Exception e) {
                dataTableResponse=new DataTableResponse();
                dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}
		
		}else {
			dataTableResponse=new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}
		
		return dataTableResponse;
	}

	@DeleteMapping(value="/deleteActivityByID/{legalEntityCode}/{activityId}/{userId}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteActivityByID(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("activityId") String activityId, @PathVariable("userId") String userId){
	ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode !=null  && !BankAuditUtil.isEmptyString(activityId)){
			try {
				maintAuditActivityService.deleteActivityByID(legalEntityCode, activityId, userId);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("Record deleted successfully.");
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				e.printStackTrace();
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
				serviceStatus.setMessage("failure");
			}
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}
	
	
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAuditActivity(@RequestBody MaintAuditActivity maintAuditActivity){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintAuditActivity!=null
				&& maintAuditActivity.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintAuditActivity.getActivityName())
				&& !BankAuditUtil.isEmptyString(maintAuditActivity.getActivityCode())
				&& !BankAuditUtil.isEmptyString(maintAuditActivity.getMaker())
				){
			
			try {
				if(maintAuditActivity.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
					maintAuditActivity.setCheckerTimestamp(new Date());
				} else{
					maintAuditActivity.setMakerTimestamp(new Date());
				}  
			    maintAuditActivityService.updateMaintAuditActivity(maintAuditActivity);
			    
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully updated");
		
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
					serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
				}
			}
			
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid payload ");
		}
		
		return serviceStatus;
	}
	
	
}
