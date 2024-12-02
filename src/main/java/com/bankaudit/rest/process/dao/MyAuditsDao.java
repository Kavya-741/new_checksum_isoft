package com.bankaudit.rest.process.dao;

import com.bankaudit.dao.Dao;
import com.bankaudit.dto.DataTableResponse;

public interface MyAuditsDao extends Dao {
	DataTableResponse getMuAuditsList(int legalEntityCode,String userId, String unitCode, String roleId, String search,Integer orderColumn,	String orderDirection, Integer page, Integer size);
}
