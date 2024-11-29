package com.bankaudit.rest.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import com.bankaudit.dto.MaintEntityAuditSubGroupMappingDto;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;
import com.bankaudit.service.MaintEntityAuditSubgroupMappingService;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.dto.ServiceStatus;

@RestController
@RequestMapping("/maintEntityAuditSubGroupMapping")
public class MaintEntityAuditSubGroupMappingController {
	
	@Autowired
	MaintEntityAuditSubgroupMappingService maintEntityAuditSubgroupMappingService;
	
	static final Logger logger = Logger.getLogger(MaintEntityAuditSubGroupMappingController.class);
	
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintEntityAuditSubgroupMappings(@RequestBody MaintEntityAuditSubGroupMappingDto maintEntityAuditSubgroupMappingDto){
		ServiceStatus serviceStatus=new ServiceStatus();
		logger.info("Inside createMaintEntityAuditSubgroupMappings ..");
		if(maintEntityAuditSubgroupMappingDto!=null
				&& maintEntityAuditSubgroupMappingDto.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getMappingType())
				//&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getEntityOrUser())
				&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getMaker())
				){
			try {
				maintEntityAuditSubgroupMappingDto.setMakerTimestamp(new Date());
				maintEntityAuditSubgroupMappingService.createMaintEntityAuditSubgroupMappings(maintEntityAuditSubgroupMappingDto);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully saved");
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
	ServiceStatus updateMaintEntityAuditSubgroupMappings(@RequestBody MaintEntityAuditSubGroupMappingDto maintEntityAuditSubgroupMappingDto){
		ServiceStatus serviceStatus=new ServiceStatus();
		logger.info("Inside updateMaintEntityAuditSubgroupMappings ..");
		if(maintEntityAuditSubgroupMappingDto!=null
				&& maintEntityAuditSubgroupMappingDto.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getMappingType())
				//&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getEntityOrUser())
				&& !BankAuditUtil.isEmptyString(maintEntityAuditSubgroupMappingDto.getMaker())
				){
			try {
				logger.info("Status .."+maintEntityAuditSubgroupMappingDto.getStatus());
				if(maintEntityAuditSubgroupMappingDto.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
					maintEntityAuditSubgroupMappingDto.setCheckerTimestamp(new Date());
				}else{
					maintEntityAuditSubgroupMappingDto.setMakerTimestamp(new Date());
				}
				maintEntityAuditSubgroupMappingService.updateMaintEntityAuditSubgroupMappings(maintEntityAuditSubgroupMappingDto);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully updated");
				
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
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
	
	@GetMapping(value="/getAll",produces=MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getMaintEntityAuditSubgroupMapping(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam("userId") String userId
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
				dataTableResponse = maintEntityAuditSubgroupMappingService.getmaintEntityAuditSubgroupMapping(legalEntityCode,userId,search,orderColumn,orderDirection,page,size);
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
	
	@GetMapping(value="/getMaintEntityAuditSubgroupMapping/{legalEntityCode}/{mappingType}/{id}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintEntityAuditSubgroupMapping(
			@PathVariable("legalEntityCode") Integer legalEntityCode, 
			@PathVariable("mappingType") String mappingType,
			@PathVariable("id") String entityOrUseid,
			@PathVariable("status") String status){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				){
			try {
				List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings=
						maintEntityAuditSubgroupMappingService.getMaintEntityAuditSubgroupMapping(legalEntityCode, mappingType, entityOrUseid, status);
				
				if(maintEntityAuditSubgroupMappings!=null&&!maintEntityAuditSubgroupMappings.isEmpty()){
					serviceStatus.setResult(maintEntityAuditSubgroupMappings);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved ");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage(" not found any");
				}
				
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid parameter");
		}
				return serviceStatus;
	}
	
	@GetMapping(value="/getMaintEntityAuditSubgroupMappingIdWithName/{legalEntityCode}/{mappingType}/{id}/{auditTypeCode}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintEntityAuditSubgroupMappingIdWithName(
			@PathVariable("legalEntityCode") Integer legalEntityCode, 
			@PathVariable("mappingType") String mappingType,
			@PathVariable("id") String entityOrUseid,
			@PathVariable(required=false,value="auditTypeCode") String auditTypeCode,
			@PathVariable("status") String status){ 
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				){
			try {
				List<MaintEntityAuditSubgroupMapping> maintEntityAuditSubgroupMappings=
						maintEntityAuditSubgroupMappingService.getMaintEntityAuditSubgroupMappingIdWithName(legalEntityCode, mappingType, entityOrUseid, auditTypeCode, status);
				
				if(maintEntityAuditSubgroupMappings!=null&&!maintEntityAuditSubgroupMappings.isEmpty()){
					serviceStatus.setResult(maintEntityAuditSubgroupMappings);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved ");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage(" not found any");
				}
				
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid parameter");
		}
				return serviceStatus;
	}
	
	@GetMapping(value="/getEntityOrUserByLevel/{legalEntityCode}/{levelCode}/{mappingType}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getEntityOrUserByLevel(
			@PathVariable("legalEntityCode") Integer legalEntityCode, 
			@PathVariable("mappingType") String mappingType,
			@PathVariable("levelCode") String levelCode,
			@PathVariable("status") String status){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				){
			try {
				List<MaintEntityAuditSubGroupMappingDto> mappingDto=
						maintEntityAuditSubgroupMappingService.getEntityOrUserByLevel(legalEntityCode, levelCode, mappingType, status);
				
				if(mappingDto!=null && !mappingDto.isEmpty()){
					serviceStatus.setResult(mappingDto);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfuly retrieved ");
				}else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage(" not found any");
				}
				
			} catch (Exception e) {
				serviceStatus.setStatus("failure");
				serviceStatus.setMessage("failure");
				e.printStackTrace();
			}
			
		}else {
			serviceStatus.setStatus("failure");
			serviceStatus.setMessage("invalid parameter");
		}
				return serviceStatus;
	}
	
	
}
