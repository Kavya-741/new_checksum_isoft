package com.bankaudit.service;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditDocumentDetails;

public interface AuditTypeDocumentDetailsService {

	void updateAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails);

	DataTableResponse getAuditTypeDocumentDetails(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size, String documentType, String documentSubType);

	// new
	Map<String, Integer> uploadMultipleDocument(Integer legalEntityCode, String auditTypeCode, String documentType,
			String documentName,
			String maker, String userRoleId, CommonsMultipartFile[] files, String documentSubType);


	String updateAuthorizeAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails);

	String deleteAuditTypeDocumentFromUI(Integer legalEntityCode, Integer id, String auditTypeCode, String status,
			String entityStatus, String documentType, String documentSubType);

	String deleteAllAuditTypeDocument(Integer legalEntityCode, String auditTypeCode, String documentType, String status,
			String documentSubType);

	AuditDocumentDetails getAuditTypeDocumentDetailByAuditType(Integer legalEntityCode, String auditTypeCode,
			String status, String documentType, String documentSubType);

}
