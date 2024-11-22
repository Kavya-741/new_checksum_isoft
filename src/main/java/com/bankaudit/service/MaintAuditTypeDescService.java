package com.bankaudit.service;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintAuditTypeDesc;

public interface MaintAuditTypeDescService {

	DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	List<MaintAuditTypeDesc> getMaintAuditTypeDescByLegalEntityCode(Integer legalEntityCode);

}
