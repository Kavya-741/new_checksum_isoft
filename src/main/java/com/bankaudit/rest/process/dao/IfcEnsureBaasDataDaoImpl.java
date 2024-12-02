package com.bankaudit.rest.process.dao;
  
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.AbstractDao;
import com.bankaudit.process.model.AuditLevelDocumentDetails;
import com.bankaudit.util.BankAuditUtil;

@Repository("ifcEnsureBaasDataDao")
public class IfcEnsureBaasDataDaoImpl extends AbstractDao implements IfcEnsureBaasDataDao {


	static final Logger logger = Logger.getLogger(IfcEnsureBaasDataDaoImpl.class);	

		
	@SuppressWarnings("deprecation")
	@Override
	public List<AuditLevelDocumentDetails> getAuditLevelDocuments(Integer legalEntityCode, String auditTypeCode,
			  String auditId, String planId,  String documentType){
		System.out.println("legalEntityCode=>"+legalEntityCode+"auditTypeCode"+auditTypeCode+"auditId"+auditId+"planId"+planId+"");
		Session session = getSession();
		session.setDefaultReadOnly(true);
		StringBuilder queryString=new StringBuilder(" Select doc from "
				+ " AuditLevelDocumentDetails doc  "
				+ " where "
				+ " doc.legalEntityCode =:legalEntityCode "					 
				+ " and doc.auditTypeCode =:auditTypeCode "
				+ " and doc.auditId =:auditId "
				+ " and doc.planId =:planId "
				+ " and doc.uploadStatus=:uploadStatus  ");
		
		if(!BankAuditUtil.isEmptyString(documentType)){
			queryString.append("and doc.documentType=:documentType");
		}
		
		Query query= session.createQuery(queryString.toString())
		 .setParameter("legalEntityCode", legalEntityCode)
		 .setParameter("auditTypeCode", auditTypeCode)
		 .setParameter("auditId", auditId)
		 .setParameter("planId", planId)	
		 .setParameter("uploadStatus", BankAuditConstant.SUCCESS);
		if(!BankAuditUtil.isEmptyString(documentType)){
			query.setParameter("documentType", documentType);
		}
		
		return query.list();		
	}
	
}