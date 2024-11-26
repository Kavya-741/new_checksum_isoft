package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintAuditFinding;

public interface MaintAuditFindingService {

	List<MaintAuditFinding> getMaintAuditFinding(Integer legalEntityCode);

	Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode);

	void createMaintAuditFinding(MaintAuditFinding maintAuditFinding) throws Exception;

	void deleteFindingByID(Integer legalEntityCode, String findingId, String userId);

}
