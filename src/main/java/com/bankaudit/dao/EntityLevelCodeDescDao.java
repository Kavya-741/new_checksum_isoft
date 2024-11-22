package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.EntityLevelCodeDesc;

public interface EntityLevelCodeDescDao extends Dao{

	Integer getEntityLevelCodeDescCount(Integer legalEntityCode);

	DataTableResponse getEntityLevelCodeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	void deleteEntityLevelCodeDesc(Integer legalEntityCode, String levelCode, String statusAuth);
	
	List<EntityLevelCodeDesc> getEntityLevelCodesDescByUserID(Integer legalEntityCode, String userId);
}
