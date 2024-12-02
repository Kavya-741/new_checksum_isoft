package com.bankaudit.rest.process.service;

import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dao.MaintEntityDao;
import com.bankaudit.process.model.IfcEnsureBaasData;
import com.bankaudit.rest.process.dao.InterfaceParameterDao;

@Service("interfaceParameterService")
@Transactional("transactionManager")
public class InterfaceParameterServiceImpl implements InterfaceParameterService{

	@Autowired
	InterfaceParameterDao interfaceParameterDao;
	
	@Autowired
	MaintEntityDao maintEntityDao;
	
	static final Logger logger = Logger.getLogger(InterfaceParameterServiceImpl.class);
	
	@Override
	public List<IfcEnsureBaasData> getLatestParametersByAuditId(int legalEntityCode, String auditId, String unitId) {
		List<IfcEnsureBaasData> ifcEnsureBaasDataLst = interfaceParameterDao.getLatestParametersByAuditId(legalEntityCode, auditId, unitId);
		return ifcEnsureBaasDataLst; 
	}
	 
}
