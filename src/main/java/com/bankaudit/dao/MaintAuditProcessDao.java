package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintAuditProcessDao  extends Dao{

    Boolean isMaintAuditProcess(Integer legalEntityCode, String processCode);

    DataTableResponse getMaintAuditProcess(Integer legalEntityCode, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);

    void deleteMaintAuditProcess(Integer legalEntityCode, String processId, String statusUnauth);

}
