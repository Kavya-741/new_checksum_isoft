package com.bankaudit.dao;

import java.util.List;
import java.util.Map;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditParameter;
import com.bankaudit.model.MaintAuditParameterWrk;

public interface MaintAuditParameterDao extends Dao{
	
	DataTableResponse getAuditParameter(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	
	List<MaintAuditParameter> getMaintAuditParameterbyAuditType(Integer legalEntityCode, String auditTypeCode);

	void deleteMaintAuditParameter(Integer legalEntityCode, String auditTypeCode, Integer id, String statusUnauth);

	boolean updateMaintAuditsParameters(List<MaintAuditParameter> maintAuditParameterList);
	
	List<Object[]> getMaintAuditParametersPreviousRatingLov(Integer legalEntityCode, String auditTypeCode);
	
	boolean updateMaintAuditsParametersDerived(Integer legalEntityCode, String auditTypeCode);

	Map<String, Object> isMaintAuditParameterAlreadyExist(MaintAuditParameterWrk maintAuditParameter);
	
	DataTableResponse getAuditParameterDerived(Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);		

}
