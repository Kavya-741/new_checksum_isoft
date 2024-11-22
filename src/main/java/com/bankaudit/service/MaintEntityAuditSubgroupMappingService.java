package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintEntityAuditSubgroupMapping;

public interface MaintEntityAuditSubgroupMappingService {

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType, String id, String auditTypeCode, String status);

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMapping(Integer legalEntityCode,
			String mappingType, String id, String status);

	void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode, String mappingType, String id);

}
