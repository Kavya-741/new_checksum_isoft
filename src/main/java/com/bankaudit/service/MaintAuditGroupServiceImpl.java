/*
 * 
 */
package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintAuditGroupDao;
import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditGroup;
import com.bankaudit.helper.BankAuditUtil;


@Service("maintAuditGroupService")
@Transactional("transactionManager")
public class MaintAuditGroupServiceImpl implements MaintAuditGroupService{


	@Autowired
	MaintAuditGroupDao maintAuditGroupDao;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaintAuditGroup> getMaintAuditGroups(Integer legalEntityCode, String auditTypeCode,
			String auditGroupCode) {
		
		Map<String, Object> properties=new HashMap<String,Object>();
		properties.put("legalEntityCode", legalEntityCode);
		if(!BankAuditUtil.isEmptyString(auditTypeCode)){
			
			properties.put("auditTypeCode", auditTypeCode);
		}
        if(!BankAuditUtil.isEmptyString(auditGroupCode)){
        	properties.put("auditGroupCode", auditGroupCode);
		}else {
			properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		}
		
        List<MaintAuditGroup> auditGroups =  maintAuditGroupDao.getEntitiesByMatchingProperties(MaintAuditGroup.class, properties);
      
     for (MaintAuditGroup maintAuditGroup : auditGroups) {
    	 Hibernate.initialize(maintAuditGroup.getMaintAuditSubgroups());
	}
       
        return auditGroups;

	
	}

	
}
