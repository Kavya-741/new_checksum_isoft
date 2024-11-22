package com.bankaudit.dao;

import java.util.List;
import java.util.Map;

import com.bankaudit.dto.DataTableResponse;
import com.bankaudit.model.UserRoleMapping;

public interface MaintUsergroupRolesDao extends Dao {

	List<UserRoleMapping> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId,String  status);

	DataTableResponse getMaintUsergroupRoles(Integer legalEntityCode,String userId, String search, Integer orderColumn,
			String orderDirection, Integer page, Integer size);
	
	void deleteRolesByUser(Integer legalEntityCode , String userId );

	void deleteMaintUsergroupRoles(Integer legalEntityCode, String ugRoleCode,String status);

	void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status);
	
	List<Map<String,Integer>> getTotalUsersPerRole(Integer legalEntityCode);

}
