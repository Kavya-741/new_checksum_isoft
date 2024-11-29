package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditSubgroup;

public interface MaintAuditSubgroupService {

	List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode);

	List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode);

	void createMaintAuditSubgroup(MaintAuditSubgroup maintAuditSubgroup)throws Exception;

	void updateMaintAuditSubgroup(MaintAuditSubgroup maintAuditSubgroup);

	MaintAuditSubgroup getMaintAuditSubgroup(Integer legalEntityCode, String auditSubGroupCode, String auditGroupCode,
			String auditTypeCode,String status);

	DataTableResponse getMaintAuditSubgroupes(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	Boolean isMaintAuditSubgroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode);
	
	Boolean isMaintAuditSubgroupLE(Integer legalEntityCode, String auditSubGroupCode);

	void deleteAuditSubGroupByID(Integer legalEntityCode, String auditTypeCode, String auditGroupCode, String auditSubGroupCode, String userId);


}
