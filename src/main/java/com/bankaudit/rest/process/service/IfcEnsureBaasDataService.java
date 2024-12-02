package com.bankaudit.rest.process.service;

import java.util.List;

import com.bankaudit.process.model.AuditLevelDocumentDetails;

public interface IfcEnsureBaasDataService {

    List<AuditLevelDocumentDetails> getAuditLevelDocuments(Integer legalEntityCode, String auditTypeCode,
            String auditId, String planId, String documentType);

}
