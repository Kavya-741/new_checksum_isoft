package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.dto.Response.DataTableResponse;
import com.finakon.baas.entities.MaintAuditTypeDesc;

public interface MaintAuditTypeDescService {

	DataTableResponse getMaintAuditTypeDesc(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

	List<MaintAuditTypeDesc> getMaintAuditTypeDescByLegalEntityCode(Integer legalEntityCode);

}
