package com.bankaudit.dao;

import java.util.List;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.MaintCriticality;

public interface MaintCriticalityDao extends Dao {

    DataTableResponse getAllMaintCriticality(String search, Integer orderColumn, String orderDirection, Integer page,
            Integer size, Integer legalEntityCode);

    List<MaintCriticality> getByCriticalityScore(Integer legalEntityCode, String criticalityOfType, String score,
            String criticalityCode);

    void deleteMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType,
            String statusAuth);

    Boolean isMaintCriticality(Integer legalEntityCode, String criticalityCode, String criticalityOfType);

    Boolean validateScore(MaintCriticality maintCriticality);
}
