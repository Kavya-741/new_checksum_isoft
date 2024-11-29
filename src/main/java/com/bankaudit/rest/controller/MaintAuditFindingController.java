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
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditFinding;
import com.bankaudit.service.MaintAuditFindingService;
import com.bankaudit.service.SequenceAppenderService;

@RestController
@RequestMapping("/api/maintAuditFinding")
public class MaintAuditFindingController {

	@Autowired
	MaintAuditFindingService maintAuditFindingService;

	@Autowired
	SequenceAppenderService sequenceAppenderService;

	static final Logger logger = Logger.getLogger(MaintAuditFindingController.class);

	@GetMapping(value = "/get/{legalEntityCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditFinding(@PathVariable("legalEntityCode") Integer legalEntityCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null) {

			try {
				List<MaintAuditFinding> maintAuditFindings = maintAuditFindingService
						.getMaintAuditFinding(legalEntityCode);
				if (maintAuditFindings != null && !maintAuditFindings.isEmpty()) {
					serviceStatus.setResult(maintAuditFindings);
					serviceStatus.setStatus("success");
					serviceStatus.setMessage("successfully retrieved ");
				} else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditFinding not found");
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

	@GetMapping(value = "/isMaintAuditFinding/{legalEntityCode}/{findingCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus isMaintAuditFinding(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("findingCode") String findingCode) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null
				&& !BankAuditUtil.isEmptyString(findingCode)) {

			try {
				Boolean maintAuditFinding = maintAuditFindingService.isMaintAuditFinding(legalEntityCode, findingCode);
				serviceStatus.setResult(maintAuditFinding);
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

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus createMaintAuditFinding(@RequestBody MaintAuditFinding maintAuditFinding) {
		ServiceStatus serviceStatus = new ServiceStatus();

		try {
			if (maintAuditFinding != null
					&& maintAuditFinding.getLegalEntityCode() != null
					&& !BankAuditUtil.isEmptyString(maintAuditFinding.getFindingCode())
					&& !BankAuditUtil.isEmptyString(maintAuditFinding.getFindingName())
					&& !BankAuditUtil.isEmptyString(maintAuditFinding.getMaker())) {

				maintAuditFinding.setFindingId(sequenceAppenderService.getAutoSequenceId());
				maintAuditFinding.setMakerTimestamp(new Date());

				maintAuditFindingService.createMaintAuditFinding(maintAuditFinding);
				serviceStatus.setStatus("success");
				serviceStatus.setMessage("successfully created");

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

	@DeleteMapping(value = "/deleteFindingByID/{legalEntityCode}/{findingId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus deleteFindingByID(@PathVariable("legalEntityCode") Integer legalEntityCode,
			@PathVariable("findingId") String findingId, @PathVariable("userId") String userId) {
		ServiceStatus serviceStatus = new ServiceStatus();

		if (legalEntityCode != null && !BankAuditUtil.isEmptyString(findingId)) {
			try {
				maintAuditFindingService.deleteFindingByID(legalEntityCode, findingId, userId);
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


	@PutMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus updateMaintAuditFinding(@RequestBody MaintAuditFinding maintAuditFinding){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(maintAuditFinding!=null
				&& maintAuditFinding.getLegalEntityCode()!=null
				&& !BankAuditUtil.isEmptyString(maintAuditFinding.getFindingName())
				&& !BankAuditUtil.isEmptyString(maintAuditFinding.getFindingCode())
				&& !BankAuditUtil.isEmptyString(maintAuditFinding.getMaker())
				){
			try {

				if(maintAuditFinding.getStatus().equals(BankAuditConstant.STATUS_AUTH)){
					maintAuditFinding.setCheckerTimestamp(new Date());
				}else{
					maintAuditFinding.setMakerTimestamp(new Date());
				} 			    
				    maintAuditFindingService.updateMaintAuditFinding(maintAuditFinding);
				    
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
	DataTableResponse getMaintAuditFindings(
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
				dataTableResponse = maintAuditFindingService.getMaintAuditFindings(legalEntityCode,search,orderColumn,orderDirection,page,size);
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

	@GetMapping(value="/getByLegalEntityCodeAndFindingCode/{legalEntityCode}/{findingId}/{status}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditFinding(@PathVariable("legalEntityCode")Integer legalEntityCode,
			@PathVariable("findingId") String findingId,
			@PathVariable("status") String status
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				&& !BankAuditUtil.isEmptyString(findingId)){
			
			try {
			    MaintAuditFinding maintAuditFinding=maintAuditFindingService.getMaintAuditFinding(legalEntityCode,findingId,status);
			    if(maintAuditFinding!=null){
			    	serviceStatus.setResult(maintAuditFinding);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("MaintAuditFinding not found");
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
