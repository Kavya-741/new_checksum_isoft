/*
 * 
 */
package com.bankaudit.rest;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintCriticality;
import com.bankaudit.service.MaintCriticalityService;


@RestController
@RequestMapping("/maintCriticality")
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
	
}
