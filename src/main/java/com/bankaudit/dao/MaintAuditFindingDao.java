package com.bankaudit.dao;

import com.bankaudit.dto.DataTableResponse;

public interface MaintAuditFindingDao extends Dao {

    Boolean isMaintAuditFinding(Integer legalEntityCode, String findingCode);

    void deleteMaintAuditFinding(Integer legalEntityCode, String findingId, String statusUnauth);

}
