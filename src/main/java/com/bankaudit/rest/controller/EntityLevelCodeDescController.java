/*
 * 
 */
package com.bankaudit.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.EntityLevelCodeDesc;
import com.bankaudit.service.EntityLevelCodeDescService;
/**
 * The Class {@link EntityLevelCodeDescController} provides the REST services to
 * manage the {@link EntityLevelCodeDesc} class objects .
 *
 * @author amit.patel
 * @version 1.0
 */

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
}
