package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintAuditSubgroup;

public interface MaintAuditSubgroupService {

	List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode);

	List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode);

}
