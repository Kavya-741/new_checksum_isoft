package com.bankaudit.rest.process.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Repository;

import com.bankaudit.dao.AbstractDao;
import com.bankaudit.process.model.IfcEnsureBaasData;

@Repository
public class InterfaceParameterDaoImpl extends AbstractDao implements InterfaceParameterDao{
	
	static final Logger logger = Logger.getLogger(InterfaceParameterDaoImpl.class);
	
 	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<IfcEnsureBaasData> getLatestParametersByAuditId(int legalEntityCode, String auditId, String unitId) {
		logger.info("Inside getLatestParametersByAuditId .. "+legalEntityCode +" auditId.."+auditId + " unitId.. "+unitId);
		Session session = getSession();
		session.setDefaultReadOnly(true);
		List<IfcEnsureBaasData> ifcEnsureBaasDataLst = new ArrayList<>();
		try {
			StringBuilder queryString=new StringBuilder("select ibd as ifcEnsureBaasData, "   // 0
					+ "   ipm.parameterDescription as parameterDescriptionTmp "  // 1
					+ " from IfcEnsureBaasData ibd , IfcParameterMapping ipm  " 
					+ " where ibd.legalEntityCode=ipm.legalEntityCode and ibd.parameterNameBaas=ipm.parameterNameBaas  " 
					+ "     and ibd.stageOfFile='L' and ibd.legalEntityCode =:legalEntityCode and ibd.auditId=:auditId ");
				
			@SuppressWarnings("deprecation")
			Query query=(Query) session.createQuery(queryString.toString())				
					.setResultTransformer(new ResultTransformer() {
						@Override
						public Object transformTuple(Object[] tuple, String[] aliases) {
							IfcEnsureBaasData ifcEnsureBaasData=null;
							if(tuple[0]!=null){
								ifcEnsureBaasData=(IfcEnsureBaasData)tuple[0];
								if(tuple[1]!=null) ifcEnsureBaasData.setParameterDescription(tuple[1].toString());
							}
							return ifcEnsureBaasData;
						}
				@Override
				public List transformList(List collection) {
					return collection;
				}
			}).setParameter("legalEntityCode",legalEntityCode).setParameter("auditId",auditId);
			ifcEnsureBaasDataLst= query.list();
			
			logger.info("ifcEnsureBaasDataLst .. "+ ifcEnsureBaasDataLst);
		}catch(Exception e) {
			e.printStackTrace(System.out);
		}
		return ifcEnsureBaasDataLst;
	}
}
