/*
 * 
 */
package com.bankaudit.service;

import java.util.List;
import com.bankaudit.model.MaintAuditGroup;

public interface MaintAuditGroupService {
	List<MaintAuditGroup> getMaintAuditGroups(Integer legalEntityCode, String auditTypeCode, String auditGroupCode);
}
