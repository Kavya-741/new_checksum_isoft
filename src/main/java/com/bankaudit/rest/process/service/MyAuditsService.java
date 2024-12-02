package com.bankaudit.rest.process.service;

import com.bankaudit.dto.DataTableResponse;

public interface MyAuditsService {

	 DataTableResponse  getMuAuditsList(int legalEntityCode,String userId, String unitCode, String roleId, String search,Integer orderColumn,
				String orderDirection, Integer page, Integer size);
	
}
