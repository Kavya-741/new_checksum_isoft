package com.bankaudit.rest.process.dao;

import java.util.List;

import com.bankaudit.dao.Dao;
import com.bankaudit.process.model.IfcEnsureBaasData;

public interface InterfaceParameterDao extends Dao {

	List<IfcEnsureBaasData> getLatestParametersByAuditId(int legalEntityCode, String auditId, String unitId);

}
