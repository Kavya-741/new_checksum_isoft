/*
 * 
 */
package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditGroup;

public interface MaintAuditGroupService {
	List<MaintAuditGroup> getMaintAuditGroups(Integer legalEntityCode, String auditTypeCode, String auditGroupCode);

	DataTableResponse getMaintAuditGroups(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	void createMaintAuditGroup(MaintAuditGroup maintAuditGroup)throws Exception;

	void updateMaintAuditGroup(MaintAuditGroup maintAuditGroup);

	MaintAuditGroup getMaintAuditGroup(Integer legalEntityCode, String auditGroupCode,
			String auditTypeCode,String status);

	Boolean isMaintAuditGroupLE(Integer legalEntityCode, String auditGroupCode);

	void deleteAuditGroupByID(Integer legalEntityCode, String auditTypeCode, String auditGroupCode, String userId);

	Boolean isMaintAuditGroup(Integer legalEntityCode, String auditTypeCode, String auditGroupCode);

}
