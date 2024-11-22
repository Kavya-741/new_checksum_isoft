/*
 * 
 */
package com.bankaudit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.constants.BankAuditConstant;
import com.bankaudit.dao.EntityLevelCodeDescDao;
import com.bankaudit.model.EntityLevelCodeDesc;

/**
 * The Class {@link EntityLevelCodeDescServiceImpl} Implements the business
 * logic method.
 *
 * @author amit.patel
 * @version 1.0
 */
@Service("entityLevelCodeDescService")
@Transactional("transactionManager")
public class EntityLevelCodeDescServiceImpl implements EntityLevelCodeDescService {

	/**
	 * The entity level code desc dao is autowired and make methods available
	 * from dao layer .
	 */
	@Autowired
	EntityLevelCodeDescDao entityLevelCodeDescDao;
	

	
	static final Logger logger = Logger.getLogger(EntityLevelCodeDescServiceImpl.class);
	


	/* (non-Javadoc)
	 * @see com.bankaudit.service.EntityLevelCodeDescService#getEntityLevelCodeDescByLegalEntityCode(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EntityLevelCodeDesc> getEntityLevelCodeDescByLegalEntityCode(Integer legalEntityCode) {
		
		Map<String, Object> properties=new HashMap<String, Object>();
		
		properties.put("legalEntityCode", legalEntityCode);
		properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
		
		return entityLevelCodeDescDao.getEntitiesByMatchingProperties(EntityLevelCodeDesc.class, properties);
	}
	

}
