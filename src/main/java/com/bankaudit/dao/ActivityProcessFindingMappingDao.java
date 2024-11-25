package com.bankaudit.dao;

import java.util.HashMap;
import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.SequenceDto;
import com.bankaudit.model.ActivityProcessFindingMapping;

public interface ActivityProcessFindingMappingDao extends Dao{

	DataTableResponse getActivityProcessFindingMappings(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	List<String> getActivityProcessFindingMappingValues(String legalEntityCode, String auditTypeCode,
			String auditGroupCode, String auditSubGroupCode, String activityCode, String processCode,
			String findingCode,String createOrUpdate);

	void deleteActivityProcessFindingMapping(Integer legalEntityCode, String mappingId, String statusUnauth);

	Boolean isActivityProcessFindingMapping(int parseInt, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode, String activityCode, String processCode, String findingCode,String createOrUpdate, String riskBelongsTo);
	
	void updateStatusforAuthModify_E(Integer legalEntityCode, String mappingId, String statusUnauth);

	List<SequenceDto> getDynamicValuesForSequence(String legalEntityCode, String auditTypeCode, String auditGroupCode, String auditSubGroupCode,
			String activityCode, String processCode);
	
	List<ActivityProcessFindingMapping> getAnyOneAuditGrpAndSubGrpForAuditType(Integer legalEntityCode,String auditTypeCode,String status);

}
