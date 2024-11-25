/*
 * 
 */
package com.bankaudit.rest.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.MaintAuditGroup;
import com.bankaudit.service.MaintAuditGroupService;
import com.bankaudit.service.SequenceAppenderService;
import com.bankaudit.helper.BankAuditUtil;

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
	
}
