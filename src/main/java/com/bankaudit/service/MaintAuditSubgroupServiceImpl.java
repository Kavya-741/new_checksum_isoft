package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditSubgroupDao;
import com.bankaudit.helper.BankAuditUtil;
import com.bankaudit.model.MaintAuditSubgroup;

@Service
@Transactional("transactionManager")
public class MaintAuditSubgroupServiceImpl implements MaintAuditSubgroupService {

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupes(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		
		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			properties.put("auditTypeCode", auditTypeCode);
		}
		if(!BankAuditUtil.isEmptyString(auditGroupCode)){
			properties.put("auditGroupCode", auditGroupCode);
		}
		if(!BankAuditUtil.isEmptyString(auditSubGroupCode)){
			properties.put("auditSubGroupCode", auditSubGroupCode);
		}else {
			properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		}
		
		return maintAuditSubgroupDao.getEntitiesByMatchingProperties(MaintAuditSubgroup.class, properties);
	}

	

	@Autowired
	MaintAuditSubgroupDao maintAuditSubgroupDao;
	@Override
	public List<MaintAuditSubgroup> getMaintAuditSubgroupe(Integer legalEntityCode) {
		return maintAuditSubgroupDao.getMaintAuditSubgroupe( legalEntityCode);
	}
}
