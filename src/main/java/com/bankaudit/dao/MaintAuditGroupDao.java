package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintAuditGroupDao extends Dao{

	DataTableResponse getMaintAuditGroups(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	void deleteMaintAuditGroup(Integer legalEntityCode, String auditGroupCode, String auditTypeCode, String statusAuth);

	Boolean isMaintAuditGroupLE(Integer legalEntityCode, String auditGroupCode);

	Boolean isMaintAuditGroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode);
			
}
