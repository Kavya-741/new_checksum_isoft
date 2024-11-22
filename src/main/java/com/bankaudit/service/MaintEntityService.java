package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintEntity;
import com.bankaudit.model.MaintEntityWrk;

public interface MaintEntityService {

	List<String> getSubBranchesGyUserIdOrUnitId(Integer legalEntityCode, String type, String userIdOrUnitId);

	List<MaintEntity> getMaintEntityByLegalEntityCode(Integer legalEntityCode);

	public void createMaintEntity(MaintEntityWrk maintEntityWrk);

	void updateMaintEntity(MaintEntityWrk  maintEntityWrk);

	public MaintEntity getUnique(Integer legalEntityCode, String unitCode,String status);

	DataTableResponse getMaintEntity(Integer legalEntityCode, String levelCodeStr,String userId,String parentUnitCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	Boolean isMaintEntity(Integer legalEntityCode, String unitCode);

	List<MaintEntity> getMaintEntityByLegalEntityCodeAndLevelCode(Integer legalEntityCode, String levelCode);

}
