/*
 * 
 */
package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.MaintCriticalityDao;
import com.bankaudit.model.MaintCriticality;

@Service("maintCriticalityService")
@Transactional("transactionManager")
public class MaintCriticalityServiceImpl implements MaintCriticalityService {


	@Autowired
	MaintCriticalityDao maintCriticalityDao;
	

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintCriticality> getMaintCriticalityByCriticalityOfType(Integer legalEntityCode,String criticalityOfType) {
		Map<String,Object> properties=new HashMap<>();
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("criticalityOfType", criticalityOfType);
		properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		return maintCriticalityDao.getEntitiesByMatchingProperties(MaintCriticality.class, properties);
	}

}
