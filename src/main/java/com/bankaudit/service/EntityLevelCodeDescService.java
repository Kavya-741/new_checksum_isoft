/*
 * 
 */
package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.EntityLevelCodeDesc;

public interface EntityLevelCodeDescService {

	
	/**
	 * This method is use to Gets the entity level code desc by legal entity
	 * code.
	 *
	 * @param legalEntityCode
	 *            specify the legal entity code
	 * @return the list .
	 */
	List<EntityLevelCodeDesc> getEntityLevelCodeDescByLegalEntityCode(Integer legalEntityCode);
	
}
