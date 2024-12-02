/*
 * 
 */
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
import com.bankaudit.model.MaintAuditGroup;
import com.bankaudit.service.MaintAuditGroupService;
import com.bankaudit.service.SequenceAppenderService;
import com.bankaudit.helper.BankAuditUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("api/maintAuditGroup")
public class MaintAuditGroupController {


	@Autowired
	MaintAuditGroupService maintAuditGroupService;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	static final Logger logger = Logger.getLogger(MaintAuditGroupController.class);
	
	@GetMapping(value="/get",produces=MediaType.APPLICATION_JSON_VALUE) 
	ServiceStatus getMaintAuditGroups(
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam(required=false,value="auditTypeCode") String auditTypeCode,
			@RequestParam(required=false,value="auditGroupCode") String auditGroupCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		
		if(!BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				List<MaintAuditGroup> maintAuditGroups = maintAuditGroupService.getMaintAuditGroups(legalEntityCode,auditTypeCode,auditGroupCode);
				if(maintAuditGroups!=null && !maintAuditGroups.isEmpty()){
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved");
					serviceStatus.setResult(maintAuditGroups);
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("no records found");
				}
					
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}
		
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("legalEntityCode required");
		}
		
		
		return serviceStatus;
	}

	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditGroup(@RequestBody MaintAuditGroup maintAuditGroup){
		ServiceStatus serviceStatus=new ServiceStatus();
		try {
			if(maintAuditGroup!=null
					&& maintAuditGroup.getLegalEntityCode()!=null
					&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditGroupCode())
					&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditGroupName())
					&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditTypeCode())
					&& !BankAuditUtil.isEmptyString(maintAuditGroup.getMaker())
					){ 
				

				maintAuditGroup.setMakerTimestamp(new Date());
				   
				maintAuditGroupService.createMaintAuditGroup(maintAuditGroup);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");
			} else {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("invalid payload");
			}
		}catch (Exception e) {
			serviceStatus.setStatus("failure");
			if(e instanceof org.springframework.dao.DataIntegrityViolationException) {
				serviceStatus.setMessage("DATAINTGRTY_VIOLATION");
			}else serviceStatus.setMessage("EXCP_OCCURED");
		}
		
		return serviceStatus;
	}
	
	/**
	 * This method is use to Update maint audit group.
	 *
	 * @param maintAuditGroup
	 *            specify the maint audit group
	 * @return the service status class object with response status and payload
	 *         .
	 */
	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAuditGroup(@RequestBody MaintAuditGroup maintAuditGroup){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintAuditGroup!=null
				&& maintAuditGroup.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditGroupName())
				&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditGroupCode())
				&& !BankAuditUtil.isEmptyString(maintAuditGroup.getAuditTypeCode())
				&& !BankAuditUtil.isEmptyString(maintAuditGroup.getMaker())
				){ 
		try{	
			if(maintAuditGroup.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
				maintAuditGroup.setCheckerTimestamp(new Date());
			}else{
				maintAuditGroup.setMakerTimestamp(new Date());
			}  
			    maintAuditGroupService.updateMaintAuditGroup(maintAuditGroup);
			    
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully updated");
		} catch (Exception e) {
			e.printStackTrace();
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
	

	@GetMapping(value="/getByLegalEntityCodeAndAuditGroupCodeAndAuditTypeCode/{legalEntityCode}/{auditGroupCode}/{auditTypeCode}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditGroup(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("auditGroupCode") String auditGroupCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("status") String status
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				&& !BankAuditUtil.isEmptyString(auditGroupCode)
				&& !BankAuditUtil.isEmptyString(auditTypeCode)){
			
			try {
			    MaintAuditGroup maintAuditGroup=maintAuditGroupService.getMaintAuditGroup(legalEntityCode,auditGroupCode,auditTypeCode,status);
			    if(maintAuditGroup!=null){
			    	serviceStatus.setResult(maintAuditGroup);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("MaintAuditGroup not found");
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
	

	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getMaintAuditGroups(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr){
		
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
				dataTableResponse = maintAuditGroupService.getMaintAuditGroups(legalEntityCode,search,orderColumn,orderDirection,page,size);
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

	@GetMapping(value="/isMaintAuditGroup/{legalEntityCode}/{auditTypeCode}/{auditGroupCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditGroup(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode,
			@PathVariable("auditGroupCode") String auditGroupCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				&& !BankAuditUtil.isEmptyString(auditTypeCode)){			
			try {				
			    Boolean maintAuditGroup=maintAuditGroupService.isMaintAuditGroup(legalEntityCode,auditTypeCode,auditGroupCode);
		    	serviceStatus.setResult(maintAuditGroup);
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully retrieved ");		    				
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
	
	@GetMapping(value="/isMaintAuditGroupLE/{legalEntityCode}/{auditGroupCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditGroupLE(@PathVariable("legalEntityCode")Integer legalEntityCode,@PathVariable("auditGroupCode") String auditGroupCode	){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(legalEntityCode!=null && !BankAuditUtil.isEmptyString(auditGroupCode)){
			try {
			    Boolean maintAuditGroupDuplicate=maintAuditGroupService.isMaintAuditGroupLE(legalEntityCode,auditGroupCode);
		    	serviceStatus.setResult(maintAuditGroupDuplicate);
			    serviceStatus.setStatus("success");
			    serviceStatus.setMessage("successfully retrieved ");	
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
	
	// Service to delete the Records, 
	@DeleteMapping(value="/deleteAuditGroupByID/{legalEntityCode}/{auditTypeCode}/{auditGroupCode}/{userId}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteAuditGroupByID(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("auditTypeCode") String auditTypeCode, @PathVariable("auditGroupCode") String auditGroupCode,
			@PathVariable("userId") String userId){
	ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode !=null && !BankAuditUtil.isEmptyString(auditTypeCode) && !BankAuditUtil.isEmptyString(auditGroupCode)){
			try {
				maintAuditGroupService.deleteAuditGroupByID(legalEntityCode, auditTypeCode, auditGroupCode, userId);
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

	// @RequestMapping(value="/getMappedMaintAuditGroup",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE) 
	// ServiceStatus getMappedMaintAuditGroup(
	// 		@RequestParam("legalEntityCode")Integer legalEntityCode,
	// 		@RequestParam(value="auditTypeCode") String auditTypeCode,
	// 		@RequestParam(required=false,value="unitType") String unitType){
		
	// 	ServiceStatus serviceStatus=new ServiceStatus();			
	// 	if(legalEntityCode != null && !BankAuditUtil.isEmptyString(auditTypeCode)){
	// 		try{ 
	// 			List<MaintAuditGroup> maintAuditGroups = maintAuditGroupService.getMappedMaintAuditGroup(legalEntityCode, auditTypeCode, unitType);
	// 			if(maintAuditGroups!=null && !maintAuditGroups.isEmpty()){
	// 				serviceStatus.setStatus("success");
	// 				serviceStatus.setMessage("successfuly retrieved");
	// 				serviceStatus.setResult(maintAuditGroups);
	// 			} else {
	// 				serviceStatus.setStatus("failure");
	// 				serviceStatus.setMessage("no records found");
	// 			}
	// 		}catch (Exception e) {
	// 			serviceStatus.setStatus("failure");
	// 			serviceStatus.setMessage("failure");
	// 			e.printStackTrace();
	// 		}			
	// 	}else {
	// 		serviceStatus.setStatus("failure");
	// 		serviceStatus.setMessage("legalEntityCode and auditTypeCode required");
	// 	}			
	// 	return serviceStatus;
	// }
	
}
