package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintAuditActivityDao extends Dao {

	Boolean isMaintAuditActivity(Integer legalEntityCode, String activityCode);

	DataTableResponse getMaintAuditActivities(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size );
	
	void deleteMaintAuditActivity(Integer legalEntityCode, String activityId, String statusUnauth);

}
