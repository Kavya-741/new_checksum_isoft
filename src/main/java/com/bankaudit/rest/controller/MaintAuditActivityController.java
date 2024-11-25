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
import com.bankaudit.model.MaintAuditActivity;
import com.bankaudit.service.MaintAuditActivityService;
import com.bankaudit.service.SequenceAppenderService;

@RestController
@RequestMapping("/api/maintAuditActivity")
public class MaintAuditActivityController {

	@Autowired
	MaintAuditActivityService maintAuditActivityService;
	

	@Autowired 
	SequenceAppenderService sequenceAppenderService;
	
	static final Logger logger = Logger.getLogger(MaintAuditActivityController.class);
	

	@GetMapping(value="/get/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditActivity(@PathVariable("legalEntityCode")Integer legalEntityCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				){
			
			try {
			    List<MaintAuditActivity> maintAuditActivities=maintAuditActivityService.getMaintAuditActivity(legalEntityCode);
			    if(maintAuditActivities!=null && !maintAuditActivities.isEmpty()){
			    	serviceStatus.setResult(maintAuditActivities);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("maintAuditActivity not found");
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
