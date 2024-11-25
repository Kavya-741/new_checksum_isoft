package com.bankaudit.service;

import java.util.List;

import com.bankaudit.model.MaintAuditProcess;

public interface MaintAuditProcessService {

	List<MaintAuditProcess> getMaintAuditProcess(Integer legalEntityCode);

}
