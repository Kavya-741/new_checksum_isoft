package com.finakon.baas.service.ServiceInterfaces;

import java.util.List;

import com.finakon.baas.entities.UserDeptMapping;
import com.finakon.baas.entities.UserRoleMapping;
public interface MaintUsergroupRolesService {
	List<?> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode, String status);

	List<UserRoleMapping> getUserRoleMappings(Integer legalEntityCode, String userId, String statsusUnauth);

	List<UserDeptMapping> getUserDeptMappings(Integer legalEntityCode, String userId, String statsusUnauth);

	void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status);

	void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status);
}
