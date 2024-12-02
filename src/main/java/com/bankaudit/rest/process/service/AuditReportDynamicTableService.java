package com.bankaudit.rest.process.service;

import com.bankaudit.dto.DataTableResponse;

public interface AuditReportDynamicTableService {
	
	DataTableResponse  getDynamicTables(Integer legalEntityCode, String userId, String role, String search,Integer orderColumn,
			String orderDirection, Integer page, Integer size);

}
