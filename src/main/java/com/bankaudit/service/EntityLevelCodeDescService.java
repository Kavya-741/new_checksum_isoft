/*
 * 
 */
package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.EntityLevelCodeDesc;

public interface EntityLevelCodeDescService {


	List<EntityLevelCodeDesc> getEntityLevelCodeDescByLegalEntityCode(Integer legalEntityCode);

	DataTableResponse getEntityLevelCodeDesc( Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	EntityLevelCodeDesc getEntityLevelCodeDescByLegalEntityCodeAndLevelCode(Integer legalEntityCode, String levelCode,String status);

	void deleteEntityLevelCodeDesc(Integer legalEntityCode, String levelCode, String statusAuth);
	
	List<EntityLevelCodeDesc> getEntityLevelCodesDescByUserID(Integer legalEntityCode, String userId);

	void createEntityLevelCodeDesc(EntityLevelCodeDesc entityLevelCodeDesc);

	Integer getEntityLevelCodeDescCount(Integer legalEntityCode);

	void updateEntityLevelCodeDesc(EntityLevelCodeDesc entityLevelCodeDesc);
			
}
