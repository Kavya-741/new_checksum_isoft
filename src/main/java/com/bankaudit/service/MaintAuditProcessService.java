package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditProcess;

public interface MaintAuditProcessService {

	List<MaintAuditProcess> getMaintAuditProcess(Integer legalEntityCode);

	Boolean isMaintAuditProcess(Integer legalEntityCode, String findingCode);

	void createMaintAuditProcess(MaintAuditProcess maintAuditProcess) throws Exception ;

	DataTableResponse getMaintAuditProcess(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

}
