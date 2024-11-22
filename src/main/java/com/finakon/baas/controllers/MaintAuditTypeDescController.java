package com.finakon.baas.controllers;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.helper.BankAuditUtil;
import com.finakon.baas.service.ServiceInterfaces.MaintAuditTypeDescService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    
}
