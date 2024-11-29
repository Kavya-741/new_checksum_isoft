package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.dto.MaintEntityAuditSubGroupMappingDto;
import com.bankaudit.model.MaintEntityAuditSubgroupMapping;

public interface MaintEntityAuditSubgroupMappingService {

	void createMaintEntityAuditSubgroupMapping(MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping);

	void updateMaintEntityAuditSubgroupMapping(MaintEntityAuditSubgroupMapping maintEntityAuditSubgroupMapping);

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMapping(Integer legalEntityCode,
			String mappingType ,String id,String status);

	List<MaintEntityAuditSubgroupMapping> getMaintEntityAuditSubgroupMappingIdWithName(Integer legalEntityCode,
			String mappingType ,String id,String auditTypeCode, String status);

    void deleteMaintEntityAuditSubgroupMapping(Integer legalEntityCode ,String mappingType,String id );
    

    void deleteMaintEntityAuditSubgroupMappingWrk(Integer legalEntityCode ,String mappingType,String id );
    
    void createMaintEntityAuditSubgroupMappings(MaintEntityAuditSubGroupMappingDto maintEntityAuditSubGroupMappingDto);
    
    void updateMaintEntityAuditSubgroupMappings(MaintEntityAuditSubGroupMappingDto maintEntityAuditSubGroupMappingDto);
    
    DataTableResponse getmaintEntityAuditSubgroupMapping(Integer legalEntityCode, String userId, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
    
    List<MaintEntityAuditSubGroupMappingDto> getEntityOrUserByLevel(Integer legalEntityCode, String levelCode, String mappingType ,String status);

}
