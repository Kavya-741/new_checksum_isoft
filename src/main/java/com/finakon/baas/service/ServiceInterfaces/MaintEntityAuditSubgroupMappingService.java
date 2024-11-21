package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.entities.MaintEntityAuditSubgroupMapping;

public interface MaintEntityAuditSubgroupMappingService {

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType, String id, String auditTypeCode, String status);

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMapping(Integer legalEntityCode,
			String mappingType ,String id,String status);

}
