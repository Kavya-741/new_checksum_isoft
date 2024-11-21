package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.entities.MaintAuditSubgroup;
public interface MaintAuditSubgroupService {

	List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode);

}
