package com.bankaudit.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.ServiceStatus;
import com.bankaudit.model.MaintAuditTypeDesc;
import com.bankaudit.service.MaintAuditTypeDescService;
import com.bankaudit.util.BankAuditUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/maintAuditTypeDesc")
public class MaintAuditTypeDescController {

    @Autowired
	private MaintAuditTypeDescService maintAuditTypeDescService;

	

    @CrossOrigin
	@GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE) 
	DataTableResponse getMaintAuditTypeDesc(@RequestParam("draw") String drawStr,
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
				dataTableResponse = maintAuditTypeDescService.getMaintAuditTypeDesc(legalEntityCode,search,orderColumn,orderDirection,page,size);
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


	@GetMapping(value="/getByLegalEntityCode/{legalEntityCode}",produces=MediaType.APPLICATION_JSON_VALUE)
	ServiceStatus getMaintAuditTypeDescByLegalEntityCode(@PathVariable("legalEntityCode")Integer legalEntityCode
			){
		ServiceStatus serviceStatus=new ServiceStatus();
		
		if(legalEntityCode!=null 
				){
			
			try {
			    List<MaintAuditTypeDesc> maintAuditTypeDescs=maintAuditTypeDescService.getMaintAuditTypeDescByLegalEntityCode(legalEntityCode);
			    if(maintAuditTypeDescs!=null&& !maintAuditTypeDescs.isEmpty()){
			    	serviceStatus.setResult(maintAuditTypeDescs);
				    serviceStatus.setStatus("success");
				    serviceStatus.setMessage("successfully retrieved ");	
			    }else {
					serviceStatus.setStatus("failure");
					serviceStatus.setMessage("MaintAuditTypeDescs not found");
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
