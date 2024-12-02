package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.process.model.AuditDocumentDetails;
import com.bankaudit.process.model.AuditDocumentDetailsWrk;

public interface AuditTypeDocumentDetailsDao extends Dao {
	

	AuditDocumentDetailsWrk getAuditTypeDocumentDetail(Integer legalEntityCode, Integer id, String auditTypeCode, String status);
	
	
	void deleteAuditTypeDocumentDetails(Integer legalEntityCode,Integer id, String auditTypeCode, String statusUnauth);
	
	DataTableResponse getAuditTypeDocumentDetails(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size , String documentType, String documentSubType);

	//new
	String deleteAuditTypeDocumentFromUI(Integer legalEntityCode,Integer id, String auditTypeCode, String status, String entityStatus,
			 String documentType, String documentSubType );
	
	String deleteAllAuditTypeDocument(Integer legalEntityCode, String auditTypeCode,  String documentType, String status, 
			  String documentSubType);
	
	String updateAuthorizeAuditTypeDocumentDetails(AuditDocumentDetails auditTypeDocumentDetails);
	
	void updateMaker(Integer legalEntityCode, String auditTypeCode,  String maker, String documentType, String documentSubType);
	
	//AuditTypeDocumentDetails getAuditTypeDocumentDetailByAuditType(Integer legalEntityCode,  String auditTypeCode,String status);
	AuditDocumentDetails getAuditTypeDocumentDetailByAuditType(Integer legalEntityCode,  String auditTypeCode,String status, 
			String documentType, String documentSubType);

}
