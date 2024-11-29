package com.bankaudit.rest.controller;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.helper.Constants;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityWrk;
import com.bankaudit.service.MaintEntityService;

@RestController
@RequestMapping("/api/maintEntity")
public class MaintEntityController {

	@Autowired
	MaintEntityService maintEntityService;

	static final Logger logger = Logger.getLogger(MaintEntityController.class);

	@GetMapping(value = "/getByLegalEntityCode/{legalEntityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintEntityByLegalEntityCode(
			@PathVariable("legalEntityCode") Integer legalEntityCode) {
		ServiceStatus apiResponse = new ServiceStatus();

		if (legalEntityCode != null) {
			try {
				List<MaintEntity> maintEntities = maintEntityService.getMaintEntityByLegalEntityCode(legalEntityCode);

				if (maintEntities != null && !maintEntities.isEmpty()) {
					apiResponse.setResult(maintEntities);
					apiResponse.setStatus(Constants.SUCCESS);
					apiResponse.setMessage(Constants.UserControllerErrorCode.SUCCESSFUL_RETRIEVED);
				} else {
					apiResponse.setStatus(Constants.FAILURE);
					apiResponse.setMessage(Constants.UserControllerErrorCode.NOT_FOUND);
				}

			} catch (Exception e) {
				apiResponse.setStatus(Constants.FAILURE);
				apiResponse.setMessage(Constants.FAILURE);
				e.printStackTrace();
			}

		} else {
			apiResponse.setStatus(Constants.FAILURE);
			apiResponse.setMessage(Constants.UserControllerErrorCode.INVALID_PARAMETER);
		}
		return apiResponse;
	}


	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintEntity(@RequestBody MaintEntityWrk maintEntityWrk){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintEntityWrk!=null
				/*&& maintEntityWrk!=null*/
				&& maintEntityWrk.getUnitCode()!=null
				&& maintEntityWrk.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintEntityWrk.getMaker())
				&& BankAuditUtil.isNumber(maintEntityWrk.getMobile1()) && BankAuditUtil.isNumber(maintEntityWrk.getMobile2()) && BankAuditUtil.isNumber(maintEntityWrk.getLandline1())
				){
			try {
				maintEntityWrk.setMakerTimestamp(new Date());
				maintEntityService.createMaintEntity(maintEntityWrk);
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
	ServiceStatus updateMaintEntity(@RequestBody MaintEntityWrk maintEntityWrk){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintEntityWrk!=null
				&& maintEntityWrk.getUnitCode()!=null
				&& maintEntityWrk.getLegalEntityCode()!=null
				&& BankAuditUtil.isNumber(maintEntityWrk.getMobile1()) && BankAuditUtil.isNumber(maintEntityWrk.getMobile2()) && BankAuditUtil.isNumber(maintEntityWrk.getLandline1())
				){
			try {
				if(maintEntityWrk.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
					maintEntityWrk.setCheckerTimestamp(new Date());
				}else{
					maintEntityWrk.setMakerTimestamp(new Date());
				}
				maintEntityService.updateMaintEntity(maintEntityWrk);
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
	DataTableResponse getMaintEntity(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]")String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam("levelCode") String levelCodeStr,
			@RequestParam("parentUnitCode") String parentUnitCode,
			@RequestParam("userId") String userId
			){
		
		DataTableResponse dataTableResponse=null;
		
		if(!BankAuditUtil.isEmptyString(drawStr)
				&& !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)
				&& !BankAuditUtil.isEmptyString(legalEntityCodeStr)
				&& (!BankAuditUtil.isEmptyString(levelCodeStr) || !BankAuditUtil.isEmptyString(parentUnitCode))){
			try {
				Integer legalEntityCode=Integer.parseInt(legalEntityCodeStr);
				Integer draw=Integer.parseInt(drawStr);
				Integer size=Integer.parseInt(length);
				Integer page=Integer.parseInt(start)/size;
				Integer orderColumn=null;
				if(!BankAuditUtil.isEmptyString(orderColumnStr)){
					orderColumn=Integer.parseInt(orderColumnStr);
				}
				dataTableResponse = maintEntityService.getMaintEntity(legalEntityCode,levelCodeStr,userId,parentUnitCode,search,orderColumn,orderDirection,page,size);
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
	

	@GetMapping(value="/isMaintEntity/{legalEntityCode}/{unitCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintEntity(@PathVariable("legalEntityCode")Integer legalEntityCode,

			@PathVariable("unitCode") String unitCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		if(legalEntityCode!=null 
				&&  !BankAuditUtil.isEmptyString(unitCode)){
			
			try {
			    Boolean maintEntity=maintEntityService.isMaintEntity(legalEntityCode,unitCode);
		    	serviceStatus.setResult(maintEntity);
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

	@GetMapping(value="/getByLegalEntityCodeAndLevelCode/{legalEntityCode}/{levelCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintEntityByLegalEntityCodeAndLevelCode(
			@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("levelCode")String levelCode){
		ServiceStatus serviceStatus=new ServiceStatus();
		//active inactive
		if(legalEntityCode!=null && !BankAuditUtil.isEmptyString(levelCode)){
			try {
				if("0".equalsIgnoreCase(levelCode))  // For Head office, whose parent unit is the same
					levelCode="1"; //
				List<MaintEntity> maintEntities=maintEntityService.getMaintEntityByLegalEntityCodeAndLevelCode(legalEntityCode,levelCode);
				
				if(maintEntities!=null&&!maintEntities.isEmpty()){
					serviceStatus.setResult(maintEntities);
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

	@RequestMapping(value="/getByLegalEntityCode/{legalEntityCode}/{auditTypeCode}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintEntityByLegalEntityCodeAndAuditTypeCode(
			@PathVariable("legalEntityCode") Integer legalEntityCode,@PathVariable("auditTypeCode") String auditTypeCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null
				){
			try {
				List<MaintEntity> maintEntities=maintEntityService.getMaintEntityByLegalEntityCodeAndAuditTypeCode(legalEntityCode, auditTypeCode);
				
				if(maintEntities!=null&&!maintEntities.isEmpty()){
					serviceStatus.setResult(maintEntities);
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
