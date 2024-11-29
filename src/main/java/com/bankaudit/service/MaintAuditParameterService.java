/*
 * 
 */
package com.bankaudit.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditParameter;
import com.bankaudit.model.MaintAuditParameterWrk;



public interface MaintAuditParameterService {
	

	void createMaintAddress(MaintAuditParameter maintAuditParameter) throws Exception;


	void updateMaintAuditParameter(MaintAuditParameter maintAuditParameter);

	Map<String, Object> isMaintAuditParameterAlreadyExist(MaintAuditParameterWrk maintAuditParameter);
	 
 
 	boolean updateMaintAuditsParameters(List<MaintAuditParameter> maintAuditParameterList,Date d,Timestamp t);

	DataTableResponse getAuditParameter( Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	

	List<MaintAuditParameter> getMaintAuditParameterbyAuditType(Integer legalEntityCode, String auditTypeCode);


	MaintAuditParameter getMaintAuditParameterByAuditTypeCodeCriticalityAndUnitType(Integer legalEntityCode,String auditTypeCode, Integer id, String status);

	List<Object[]> getMaintAuditParametersPreviousRatingLov(Integer legalEntityCode, String auditTypeCode);
	
	boolean updateMaintAuditsParametersDerived(Integer legalEntityCode, String auditTypeCode);


	
	DataTableResponse getAuditParameterDerived( Integer legalEntityCode,String auditTypeCode,String entitlement,String userName, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	
	void deleteMaintAuditParameter(Integer legalEntityCode, String auditTypeCode, Integer id,
			String statusAuth);
	
}
