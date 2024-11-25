package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintAuditFinding;

public interface MaintAuditFindingService {

	List<MaintAuditFinding> getMaintAuditFinding(Integer legalEntityCode);

}
