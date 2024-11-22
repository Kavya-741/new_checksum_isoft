package com.bankaudit.dao;

import com.bankaudit.model.GeneralParameter;

import java.util.List;

public interface UserDeptMappingDao extends Dao{

    List<GeneralParameter> getUserDeptByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId, String  status);

    void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status);
}
