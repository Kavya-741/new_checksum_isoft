package com.bankaudit.rest.process.controller;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.rest.process.service.MyAuditsService;
import com.bankaudit.util.BankAuditUtil;

@RestController
@RequestMapping("/api/myAudits")
public class MyAuditsController {
	@Autowired
	MyAuditsService myAuditService;
	
	static final Logger logger = Logger.getLogger(MyAuditsController.class);	
	
	@GetMapping(value = "/getMyAudits", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getAuditSchedules(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode")String legalEntityCodeStr,
			@RequestParam("userId")String userId,
			@RequestParam(value="unitCode",required=false) String unitCode,
			@RequestParam(value="roleId",required=false)String roleId){

		DataTableResponse dataTableResponse = null;

		if (!BankAuditUtil.isEmptyString(drawStr) && !BankAuditUtil.isEmptyString(length)
				&& !BankAuditUtil.isEmptyString(start)) {
			try {
				Integer draw = Integer.parseInt(drawStr);
				Integer size = Integer.parseInt(length);
				Integer page = Integer.parseInt(start) / size;
				Integer orderColumn = null;
				if (!BankAuditUtil.isEmptyString(orderColumnStr)) {
					orderColumn = Integer.parseInt(orderColumnStr);
				}
				logger.info("legalEntityCode:: "+legalEntityCodeStr +" userId:: "+ userId+" unitId:: "+unitCode+" role:: "+roleId);
				dataTableResponse = myAuditService.getMuAuditsList(Integer.parseInt(legalEntityCodeStr),userId, unitCode, roleId, search, orderColumn, orderDirection, page, size);
				logger.info("Inside controller dataTableResponse .... "+dataTableResponse);
				dataTableResponse.setDraw(draw);

			} catch (Exception e) {
				dataTableResponse = new DataTableResponse();
				dataTableResponse.setError(e.getMessage());
				e.printStackTrace();
			}

		} else {
			dataTableResponse = new DataTableResponse();
			dataTableResponse.setError("invalid data");
		}

		return dataTableResponse;
	}
}
