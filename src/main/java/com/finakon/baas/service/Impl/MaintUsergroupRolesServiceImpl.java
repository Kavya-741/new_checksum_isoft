package com.finakon.baas.service.Impl;

import java.util.*;
import com.finakon.baas.entities.MaintUsergroupRoles;
import com.finakon.baas.entities.MaintUsergroupRolesWrk;
import com.finakon.baas.entities.UserDeptMapping;
import com.finakon.baas.entities.UserDeptMappingWrk;
import com.finakon.baas.entities.UserRoleMapping;
import com.finakon.baas.entities.UserRoleMappingWrk;
import com.finakon.baas.helper.BankAuditConstant;
import com.finakon.baas.repository.JPARepositories.MaintUserGroupRolesRepository;
import com.finakon.baas.repository.JPARepositories.MaintUserGroupRolesWrkRepository;
import com.finakon.baas.repository.JPARepositories.UserDeptMappingRepository;
import com.finakon.baas.repository.JPARepositories.UserDeptMappingWrkRepository;
import com.finakon.baas.repository.JPARepositories.UserRoleMappingRepository;
import com.finakon.baas.repository.JPARepositories.UserRoleMappingWrkRepository;
import com.finakon.baas.service.ServiceInterfaces.MaintUsergroupRolesService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("transactionManager")
public class MaintUsergroupRolesServiceImpl implements MaintUsergroupRolesService {

	@Autowired
	MaintUserGroupRolesWrkRepository maintUserGroupRolesWrkRepository;

	@Autowired
	MaintUserGroupRolesRepository maintUserGroupRolesRepository;

	@Autowired
	UserRoleMappingWrkRepository userRoleMappingWrkRepository;

	@Autowired
	UserRoleMappingRepository userRoleMappingRepository;

	@Autowired
	UserDeptMappingRepository userDeptMappingRepository;

	@Autowired
	UserDeptMappingWrkRepository userDeptMappingWrkRepository;

	@Override
	public List<?> getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode,
			String status) {

		if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			List<MaintUsergroupRolesWrk> lst = new ArrayList<>();
			List<MaintUsergroupRolesWrk> listWrk = maintUserGroupRolesWrkRepository
					.findByLegalEntityCode(legalEntityCode);
			for (MaintUsergroupRolesWrk rolesWrk : listWrk) {
				if (!("SADM").equalsIgnoreCase(rolesWrk.getUgRoleCode()))
					lst.add(rolesWrk); // not to consider Super Admin Role for the Client, It is used only for internal
										// BAAS purpose
			}
			return lst;

		} else {
			List<MaintUsergroupRoles> lst = new ArrayList<>();
			List<MaintUsergroupRoles> listMst = maintUserGroupRolesRepository
					.findByLegalEntityCodeAndEntityStatus(legalEntityCode, BankAuditConstant.STATUS_ACTIVE);
			for (MaintUsergroupRoles rolesMst : listMst) {
				if (!("SADM").equalsIgnoreCase(rolesMst.getUgRoleCode()))
					lst.add(rolesMst); // not to consider Super Admin Role for the Client, It is used only for internal
										// BAAS purpose
			}
			return lst;
		}

	}

	@Override
	public List<UserRoleMapping> getUserRoleMappings(Integer legalEntityCode, String userId, String status) {

		if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			List<UserRoleMapping> userRoleMappings = null;
			List<UserRoleMappingWrk> userRoleMappingWrks = userRoleMappingWrkRepository.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
			if (userRoleMappingWrks != null && !userRoleMappingWrks.isEmpty()) {
				userRoleMappings = new ArrayList<>();
				UserRoleMapping userRoleMapping = null;
				for (UserRoleMappingWrk userRoleMappingWrk : userRoleMappingWrks) {
					userRoleMapping = new UserRoleMapping();
					BeanUtils.copyProperties(userRoleMappingWrk, userRoleMapping);
					userRoleMappings.add(userRoleMapping);
				}

			}
			return userRoleMappings;
		} else {
			return userRoleMappingRepository.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
		}
	}

	@Override
	public List<UserDeptMapping> getUserDeptMappings(Integer legalEntityCode, String userId, String status) {

		if (!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)) {
			List<UserDeptMapping> userDeptMappings = null;
			List<UserDeptMappingWrk> userDeptMappingWrks = userDeptMappingWrkRepository
					.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
			if (userDeptMappingWrks != null && !userDeptMappingWrks.isEmpty()) {
				userDeptMappings = new ArrayList<>();
				UserDeptMapping userDeptMapping = null;
				for (UserDeptMappingWrk userDeptMappingWrk : userDeptMappingWrks) {
					userDeptMapping = new UserDeptMapping();
					BeanUtils.copyProperties(userDeptMappingWrk, userDeptMapping);
					userDeptMappings.add(userDeptMapping);
				}

			}
			return userDeptMappings;
		} else {
			return userDeptMappingRepository.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bankaudit.service.MaintUsergroupRolesService#deleteUserRoleMapping(java.
	 * lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status) {

		if (!BankAuditConstant.STATUS_AUTH.equals(status)) {
			List<UserRoleMappingWrk> userRoleMappingWrk = userRoleMappingWrkRepository
					.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
			userRoleMappingWrkRepository.deleteAll(userRoleMappingWrk);
		} else {
			List<UserRoleMapping> userRoleMapping = userRoleMappingRepository.findByLegalEntityCodeAndUserId(legalEntityCode,
					userId);
			userRoleMappingRepository.deleteAll(userRoleMapping);
		}
	}

	@Override
	public void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status) {
		if (!BankAuditConstant.STATUS_AUTH.equals(status)) {
			List<UserDeptMappingWrk> userDeptWrk = userDeptMappingWrkRepository
					.findByLegalEntityCodeAndUserId(legalEntityCode, userId);
			userDeptMappingWrkRepository.deleteAll(userDeptWrk);
		} else {
			List<UserDeptMapping> userDeptWrk = userDeptMappingRepository.findByLegalEntityCodeAndUserId(legalEntityCode,
					userId);
			userDeptMappingRepository.deleteAll(userDeptWrk);
		}
	}

}
