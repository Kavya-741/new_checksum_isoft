package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintAuditGroupDao extends Dao{

	DataTableResponse getMaintAuditGroups(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
			
}
