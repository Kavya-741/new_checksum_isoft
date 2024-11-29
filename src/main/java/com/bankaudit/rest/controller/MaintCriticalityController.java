/*
 * 
 */
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintCriticality;
import com.bankaudit.service.MaintCriticalityService;


@RestController
@RequestMapping("/api/maintCriticality")
public class MaintCriticalityController {


	@Autowired
	MaintCriticalityService maintCriticalityService;
	
	/** The Constant logger is used to specify the . */
	static final Logger logger = Logger.getLogger(MaintCriticalityController.class);

	
	@GetMapping(value="/getByCriticalityOfType/{legalEntityCode}/{criticalityOfType}", produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintCriticalityByCriticalityOfType(
			@PathVariable("criticalityOfType")String criticalityOfType,
			@PathVariable("legalEntityCode")Integer legalEntityCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if( !BankAuditUtil.isEmptyString(criticalityOfType)
				&& legalEntityCode!=null ){
			try {
				List<MaintCriticality> maintCriticalities=maintCriticalityService.getMaintCriticalityByCriticalityOfType(legalEntityCode ,criticalityOfType);
				if(maintCriticalities!=null && !maintCriticalities.isEmpty()){
					serviceStatus.setResult(maintCriticalities);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved");
				}else {
					serviceStatus.setMessage("codes not found ");
					serviceStatus.setStatus("failure");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid code");
		}
		return serviceStatus;
	}

	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintCriticality(@RequestBody MaintCriticality maintCriticality){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintCriticality!=null 
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityCode())
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityOfType())
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityDesc())
				){
			try {
				Boolean validateScore= maintCriticalityService.validateScore(maintCriticality); // To validate the duplicate entry of the Score for the Criticality Type
				logger.info(" createMaintCriticality validateScore:: "+ validateScore);
				if(!validateScore)
				{
					maintCriticality.setMakerTimestamp(new Date());
					maintCriticalityService.createMaintCriticality(maintCriticality);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage(" successfully created ");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("The Score is already maintained for "+maintCriticality.getCriticalityOfType()+ " Criticality Type.");
				}
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
			serviceStatus.setMessage("invalid payload");
		}
		return serviceStatus;
	}
	
	

	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintCriticality(@RequestBody MaintCriticality maintCriticality){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintCriticality!=null 
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityCode())
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityOfType())
				&& !BankAuditUtil.isEmptyString(maintCriticality.getCriticalityDesc())
				){
			
			try {
				logger.info("inside updateMaintCriticality ....");
				Boolean validateScore = false;
				if(! "A".equals(maintCriticality.getStatus())) // The condition to be validated for a New or Updated Record, there is no validation check during Authorization.
					validateScore= maintCriticalityService.validateScore(maintCriticality); // To validate the duplicate entry of the Score for the Criticality Type
				logger.info(" updateMaintCriticality validateScore:: "+ validateScore);
				if(!validateScore)
				{
					maintCriticality.setMakerTimestamp(new Date());
					maintCriticalityService.updateMaintCriticality(maintCriticality);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage(" successfully updated ");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("The Score is already maintained for "+maintCriticality.getCriticalityOfType()+ " Criticality Type.");
				}
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
			serviceStatus.setMessage("invalid payload");
		}
		
		
		return serviceStatus;
	}
	

	@GetMapping(value="/getByCriticalityCodeAndCriticalityOfType/{legalEntityCode}/{criticalityCode}/{criticalityOfType}/{status}", produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintCriticalityByCriticalityCodeAndCriticalityOfType(
			@PathVariable("criticalityCode")String criticalityCode,
			@PathVariable("criticalityOfType")String criticalityOfType,
			@PathVariable("legalEntityCode")String legalEntityCodeStr,
			@PathVariable("status")String status
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(!BankAuditUtil.isEmptyString(criticalityCode)
				&& !BankAuditUtil.isEmptyString(criticalityOfType)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)){
			try {
				
				
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				MaintCriticality maintCriticality=maintCriticalityService.getMaintCriticalityByCriticalityCodeAndCriticalityOfType(legalEntityCode,criticalityCode,criticalityOfType,status);
				if(maintCriticality!=null){
					serviceStatus.setResult(maintCriticality);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved");
				}else {
					serviceStatus.setMessage("codes not found ");
					serviceStatus.setStatus("failure");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid code");
		}
		return serviceStatus;
	}
	
	

	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getAllMaintCriticality(
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
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)
			){
			try {
				Integer draw=Integer.parseInt(drawStr);
				Integer size=Integer.parseInt(length);
				Integer page=Integer.parseInt(start)/size;
				Integer orderColumn=null;
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				if(!BankAuditUtil.isEmptyString(orderColumnStr)){
					orderColumn=Integer.parseInt(orderColumnStr);
				}
				
				dataTableResponse = maintCriticalityService.getAllMaintCriticality(search,orderColumn,orderDirection,page,size,legalEntityCode);
				dataTableResponse.setDraw(draw);    
					
			} catch (Exception e) {
                dataTableResponse=new DataTableResponse();
                dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}
		
		}else {
			dataTableResponse=new DataTableResponse();
			dataTableResponse.setError("invalid payload");
		}
		
		return dataTableResponse;
	}
	

	@GetMapping(value="/getByCriticalityScore",produces=MediaType.APPLICATION_JSON_VALUE) 
	ServiceStatus getByCriticalityScore(
			@RequestParam("legalEntityCode") Integer legalEntityCode,
			@RequestParam("criticalityOfType") String criticalityOfType,
			@RequestParam(value="score",required=false) String score,
			@RequestParam(value="criticalityCode",required=false) String criticalityCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if( !BankAuditUtil.isEmptyString(criticalityOfType)
				&& legalEntityCode!=null ){
			try {
				List<MaintCriticality> maintCriticalities=maintCriticalityService.getByCriticalityScore(legalEntityCode ,criticalityOfType,score,criticalityCode);
				if(maintCriticalities!=null && !maintCriticalities.isEmpty()){
					serviceStatus.setResult(maintCriticalities);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved");
				}else {
					serviceStatus.setMessage("codes not found ");
					serviceStatus.setStatus("failure");
				}
			} catch (Exception e) {
				e.printStackTrace();
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid code");
		}
		return serviceStatus;
	}
	

	@GetMapping(value="/isMaintCriticality/{legalEntityCode}/{criticalityCode}/{criticalityOfType}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintCriticality(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("criticalityCode") String criticalityCode,
			@PathVariable("criticalityOfType") String criticalityOfType
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(legalEntityCode!=null 
				&& !BankAuditUtil.isEmptyString(criticalityCode)
				&&  !BankAuditUtil.isEmptyString(criticalityOfType)){
			
			try {
			    Boolean maintCriticality=maintCriticalityService.isMaintCriticality(legalEntityCode,criticalityCode,criticalityOfType);
		    	serviceStatus.setResult(maintCriticality);
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
		@DeleteMapping(value="/deleteCriticalityByCodeAndType/{legalEntityCode}/{criticalityCode}/{criticalityOfType}/{userId}",produces=MediaType.APPLICATION_JSON_VALUE)
		ServiceStatus deleteCriticalityByCodeAndType(@PathVariable("legalEntityCode")Integer legalEntityCode,
				@PathVariable("criticalityCode") String criticalityCode, 
				@PathVariable("criticalityOfType") String criticalityOfType,
				@PathVariable("userId") String userId){
		ServiceStatus serviceStatus=new ServiceStatus();
			
			if(legalEntityCode !=null  && !BankAuditUtil.isEmptyString(criticalityCode) && 
					!BankAuditUtil.isEmptyString(criticalityOfType) && !BankAuditUtil.isEmptyString(userId)){
				try {
					maintCriticalityService.deleteCriticalityByCodeAndType(legalEntityCode, criticalityCode, criticalityOfType , userId);
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
	
	
}
