package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditTypeDesc;

public interface MaintAuditTypeDescDao extends Dao {

	DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	
	List<MaintAuditTypeDesc> getAuditTypeByLEAndUser(Integer legalEntityCode,String userId,String role, List<String> deptList, boolean isDeptAsRMD);

}
