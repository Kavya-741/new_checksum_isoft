package com.bankaudit.rest.process.service;

import java.util.List;
import com.bankaudit.process.model.IfcEnsureBaasData;

public interface InterfaceParameterService {
	 	
	List<IfcEnsureBaasData> getLatestParametersByAuditId(int legalEntityCode, String auditId, String unitId);

}
