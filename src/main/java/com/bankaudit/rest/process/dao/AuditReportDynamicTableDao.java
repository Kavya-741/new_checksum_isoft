package com.bankaudit.rest.process.dao;

import com.bankaudit.dao.Dao;
import com.bankaudit.dto.DataTableResponse;

public interface AuditReportDynamicTableDao extends Dao {
	
	DataTableResponse getDynamicTables(int legalEntityCode,String userId, String role, String search,Integer orderColumn, 
			String orderDirection, Integer page, Integer size);
}
