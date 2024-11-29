package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditActivity;

public interface MaintAuditActivityService {

	List<MaintAuditActivity> getMaintAuditActivity(Integer legalEntityCode);

	Boolean isMaintAuditActivity(Integer legalEntityCode, String activityCode);

	void createMaintAuditActivity(MaintAuditActivity maintAuditActivity) throws Exception;

	DataTableResponse getMaintAuditActivities(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	
	void deleteActivityByID(Integer legalEntityCode, String activityId, String userId);

	void updateMaintAuditActivity(MaintAuditActivity maintAuditActivity);

	MaintAuditActivity getMaintAuditActivity(Integer legalEntityCode, String activityId,String status);

}
