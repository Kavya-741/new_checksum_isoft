package com.bankaudit.rest.process.dao;

import java.util.List;
import com.bankaudit.dao.Dao;
import com.bankaudit.process.model.AuditLevelDocumentDetails;

public interface IfcEnsureBaasDataDao extends Dao {

	List<AuditLevelDocumentDetails> getAuditLevelDocuments(Integer legalEntityCode, String auditTypeCode,
			String auditId, String planId, String documentType);

}
