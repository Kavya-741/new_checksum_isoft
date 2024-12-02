package com.bankaudit.rest.process.service;

import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bankaudit.process.model.AuditLevelDocumentDetails;
import com.bankaudit.rest.process.dao.IfcEnsureBaasDataDao;

@Service("ifcEnsureBaasDataService")
@Transactional("transactionManager")
public class IfcEnsureBaasDataServiceImpl  implements IfcEnsureBaasDataService{
	
	@Autowired 
	IfcEnsureBaasDataDao ifcEnsureBaasDataDao;
	
	static final Logger logger = Logger.getLogger(IfcEnsureBaasDataServiceImpl.class);

	@Override
	public List<AuditLevelDocumentDetails> getAuditLevelDocuments(Integer legalEntityCode, String auditTypeCode,
			  String auditId, String planId,  String documentType){
		return ifcEnsureBaasDataDao.getAuditLevelDocuments(legalEntityCode, auditTypeCode, auditId, planId, documentType);
	 }
 
	
}