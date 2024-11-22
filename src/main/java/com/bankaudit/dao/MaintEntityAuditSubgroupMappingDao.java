package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintEntityAuditSubgroupMappingDao extends Dao {


	void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode, String mappingType, String id);
	void deleteMaintEntityAuditSubgroupMapping(Integer legalEntityCode, String mappingType, String id);
	
	 DataTableResponse getmaintEntityAuditSubgroupMapping(Integer legalEntityCode, String userId, String search, Integer orderColumn,
				String orderDirection, Integer page, Integer size);	

	 
	 void deleteMaintEntityAuditSubgroupMappingWrkByIds(Integer legalEntityCode, String mappingType, String id, String auditTypeCode, String authUniqueId );
	 
	 void deleteMaintEntityAuditSubgroupMappingByIds(Integer legalEntityCode, String mappingType, String id, String auditTypeCode,  String authUniqueId );
		
}
