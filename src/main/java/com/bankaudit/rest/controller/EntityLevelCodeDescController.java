/*
 * 
 */
package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.EntityLevelCodeDesc;
import com.bankaudit.service.EntityLevelCodeDescService;
import com.bankaudit.util.BankAuditUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/entityLevelCodeDesc")
public class EntityLevelCodeDescController {

	@Autowired
	EntityLevelCodeDescService entityLevelCodeDescService;

	

	@GetMapping(value="/getByLegalEntityCode/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getEntityLevelCodeDescByLegalEntityCode(@PathVariable("legalEntityCode")Integer legalEntityCode){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null){
			try {
				
				List<EntityLevelCodeDesc> entityLevelCodeDescs=entityLevelCodeDescService.getEntityLevelCodeDescByLegalEntityCode(legalEntityCode);
				if(entityLevelCodeDescs!=null && !entityLevelCodeDescs.isEmpty()){
					
					serviceStatus.setStatus("success");
					serviceStatus.setResult(entityLevelCodeDescs);
					serviceStatus.setMessage("successfully retieved ");
					
				}else {
					serviceStatus.setMessage("no entityLevelCodeDescs found with legalEntityCode");
					serviceStatus.setStatus("failure");
				}
				
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

	

	@PutMapping(produces=MediaType.APPLICATION_JSON_VALUE,consumes=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateEntityLevelCodeDesc(@RequestBody EntityLevelCodeDesc entityLevelCodeDesc ){
		
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(entityLevelCodeDesc!=null
			&& entityLevelCodeDesc.getLegalEntityCode()!=null
			&& !BankAuditUtil.isEmptyString(entityLevelCodeDesc.getLevelCode())
			&& !BankAuditUtil.isEmptyString(entityLevelCodeDesc.getLevelDesc())
			&& !BankAuditUtil.isEmptyString(entityLevelCodeDesc.getStatus())
				){
			try {
				
			if(entityLevelCodeDesc.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
				entityLevelCodeDesc.setCheckerTimestamp(new Date());
			}else{
				entityLevelCodeDesc.setMakerTimestamp(new Date());
			}
			entityLevelCodeDescService.updateEntityLevelCodeDesc(entityLevelCodeDesc);
			
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

	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getMaintEntity(
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
				dataTableResponse =  entityLevelCodeDescService.getEntityLevelCodeDesc(legalEntityCode,search,orderColumn,orderDirection,page,size);
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

	@GetMapping(value="/getByLegalEntityCodeAndLevelCode/{legalEntityCode}/{levelCode}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(
			@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("levelCode")String levelCode,
			@PathVariable("status")String status){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				&& !BankAuditUtil.isEmptyString(levelCode)){
			try {
				
				EntityLevelCodeDesc entityLevelCodeDesc=entityLevelCodeDescService.getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(legalEntityCode,levelCode,status);
				if(entityLevelCodeDesc!=null){
					
					serviceStatus.setStatus("success");
					serviceStatus.setResult(entityLevelCodeDesc);
					serviceStatus.setMessage("successfully retieved ");
					
				}else {
					serviceStatus.setMessage("no entityLevelCodeDescs found with legalEntityCode");
					serviceStatus.setStatus("failure");
				}
				
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
	
	@DeleteMapping(value="/deleteEntityLevelCodeDesc/{legalEntityCode}/{levelCode}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteEntityLevelCodeDesc(
			@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("levelCode")String levelCode,
			@PathVariable("status")String status){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				&& !BankAuditUtil.isEmptyString(levelCode)){
			try {				
				 entityLevelCodeDescService.deleteEntityLevelCodeDesc(legalEntityCode,levelCode,status);	
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
	
	@GetMapping(value="/getEntityLevelCodesDescByUserID/{legalEntityCode}/{userId}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getEntityLevelCodesDescByUserID(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("userId")String userId){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null){
			try {
				
				List<EntityLevelCodeDesc> entityLevelCodeDescs=entityLevelCodeDescService.getEntityLevelCodesDescByUserID(legalEntityCode,userId);
				if(entityLevelCodeDescs!=null && !entityLevelCodeDescs.isEmpty()){
					
					serviceStatus.setStatus("success");
					serviceStatus.setResult(entityLevelCodeDescs);
					serviceStatus.setMessage("successfully retieved ");
					
				}else {
					serviceStatus.setMessage("no entityLevelCodeDescs found with legalEntityCode");
					serviceStatus.setStatus("failure");
				}
				
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
}
