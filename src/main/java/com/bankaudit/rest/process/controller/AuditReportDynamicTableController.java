package com.bankaudit.rest.process.controller;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.rest.process.service.AuditReportDynamicTableService;
import com.bankaudit.util.BankAuditUtil;

@RestController
@RequestMapping("/api/auditReportDynamicTable")
public class AuditReportDynamicTableController {

	@Autowired
	AuditReportDynamicTableService auditReportDynamicTableService;

	static final Logger logger = Logger.getLogger(AuditReportDynamicTableController.class);

	@GetMapping(value = "/getAllDynamicTables", produces = MediaType.APPLICATION_JSON_VALUE)
	DataTableResponse getDynamicTables(
			@RequestParam("draw") String drawStr,
			@RequestParam("length") String length,
			@RequestParam("start") String start,
			@RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") String orderColumnStr,
			@RequestParam("order[0][dir]") String orderDirection,
			@RequestParam("legalEntityCode") String legalEntityCodeStr,
			@RequestParam("userId") String userId,
			@RequestParam("role") String role) {

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
				logger.info("legalEntityCode:: " + legalEntityCodeStr + " userId:: " + userId + "role:: " + role);
				dataTableResponse = auditReportDynamicTableService.getDynamicTables(
						Integer.parseInt(legalEntityCodeStr), userId, role, search, orderColumn,
						orderDirection, page, size);
				logger.info("Inside controller dataTableResponse .... " + dataTableResponse);
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
