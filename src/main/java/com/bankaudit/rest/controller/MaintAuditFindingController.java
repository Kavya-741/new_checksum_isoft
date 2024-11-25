package com.bankaudit.rest.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
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
	

	@GetMapping(value="/get/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditFinding(@PathVariable("legalEntityCode")Integer legalEntityCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				){
			
			try {
			    List<MaintAuditFinding> maintAuditFindings=maintAuditFindingService.getMaintAuditFinding(legalEntityCode);
			    if(maintAuditFindings!=null && !maintAuditFindings.isEmpty()){
			    	serviceStatus.setResult(maintAuditFindings);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditFinding not found");
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
