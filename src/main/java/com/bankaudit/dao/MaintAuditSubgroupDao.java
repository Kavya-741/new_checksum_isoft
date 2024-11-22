package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditSubgroup;

public interface MaintAuditSubgroupDao extends Dao {

	DataTableResponse getMaintAuditSubgroupes(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode);

	void deleteMaintAuditSubgroup(Integer legalEntityCode, String auditSubGroupCode, String auditGroupCode,
			String auditTypeCode, String statusUnauth);

	Boolean isMaintAuditSubgroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode);
	
	Boolean isMaintAuditSubgroupLE(Integer legalEntityCode, String auditSubGroupCode);

}
