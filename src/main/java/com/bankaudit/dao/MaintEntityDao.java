package com.bankaudit.dao;

import java.util.List;
import java.util.Map;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintEntity;

public interface MaintEntityDao extends Dao {
	List<String> getSubBranchesGyUserIdOrUnitId(Integer legalEntityCode, String type, String userIdOrUnitId);

	MaintEntity getUnique(Integer legalEntityCode, String unitCode,String status);

	void deleteMaintEntityWrk(Integer legalEntityCode, String unitCode);

	DataTableResponse getMaintEntity(Integer legalEntityCode, String levelCodeStr,String userId,String parentUnitCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	Boolean isMaintEntity(Integer legalEntityCode, String unitCode);

	List<MaintEntity> getMaintEntityByLegalEntityCodeAndLevelCode(Integer legalEntityCode, String levelCode);

	
}
