package com.bankaudit.service;

import java.util.List;
import java.util.Set;

import com.bankaudit.model.MaintUsergroupRoles;
import com.bankaudit.model.UserDeptMapping;
import com.bankaudit.model.UserRoleMapping;
public interface MaintUsergroupRolesService {
	List<?> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode, String status);

	List<UserRoleMapping> getUserRoleMappings(Integer legalEntityCode, String userId, String statsusUnauth);

	List<UserDeptMapping> getUserDeptMappings(Integer legalEntityCode, String userId, String statsusUnauth);

	void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status);

	void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status);

	Set<MaintUsergroupRoles> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId,String status);

}
