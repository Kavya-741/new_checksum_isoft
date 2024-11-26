package com.bankaudit.service;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.ActivityProcessFindingMapping;

public interface ActivityProcessFindingMappingService {

	void createActivityProcessFindingMapping(ActivityProcessFindingMapping activityProcessFindingMapping)
			throws Exception;

	DataTableResponse getActivityProcessFindingMappings(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	Boolean isActivityProcessFindingMapping(int parseInt, String auditTypeCode, String auditGroupCode,
			String auditSubGroupCode, String activityCode, String processCode, String findingCode,
			String createOrUpdate, String riskBelongsTo);

	void updateActivityProcessFindingMapping(ActivityProcessFindingMapping activityProcessFindingMapping);

	public ActivityProcessFindingMapping getActivityProcessFindingMapping(Integer legalEntityCode, String mappingId,
			String status);

	void deleteActivityProcessFindingMapping(Integer legalEntityCode, String mappingId, String statusUnauth);

}
