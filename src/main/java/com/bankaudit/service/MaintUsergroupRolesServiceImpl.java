package com.bankaudit.service;

import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankaudit.dao.MaintUsergroupRolesDao;
import com.bankaudit.dao.UserDeptMappingDao;
import com.bankaudit.helper.BankAuditConstant;
import com.bankaudit.model.MaintUsergroupRoles;
import com.bankaudit.model.MaintUsergroupRolesWrk;
import com.bankaudit.model.UserDeptMapping;
import com.bankaudit.model.UserDeptMappingWrk;
import com.bankaudit.model.UserRoleMapping;
import com.bankaudit.model.UserRoleMappingWrk;

@Service
@Transactional("transactionManager")
public class MaintUsergroupRolesServiceImpl implements MaintUsergroupRolesService {

	@Autowired
	MaintUsergroupRolesDao maintUsergroupRolesDao;

	@Autowired
	UserDeptMappingDao userDeptMappingDao;

	@Override
	public List getMaintUsergroupRolesByLegalEntityCodeAndUserId(Integer legalEntityCode,
			String status) {
		Map<String,Object> properties=new HashMap<String, Object>();
		properties.put("legalEntityCode", legalEntityCode);
		List lst = new ArrayList();
		
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			List<MaintUsergroupRolesWrk> listWrk=  maintUsergroupRolesDao.getEntitiesByMatchingProperties(MaintUsergroupRolesWrk.class, properties);
			for(MaintUsergroupRolesWrk rolesWrk: listWrk) {
				if(!("SADM").equalsIgnoreCase(rolesWrk.getUgRoleCode())) lst.add(rolesWrk); //not to consider Super Admin Role for the Client, It is used only for internal BAAS purpose
			}
					
		}else {
			properties.put("entityStatus", BankAuditConstant.STATUS_ACTIVE);
			List<MaintUsergroupRoles> listMst= maintUsergroupRolesDao.getEntitiesByMatchingProperties(MaintUsergroupRoles.class, properties);
			for(MaintUsergroupRoles rolesMst: listMst) {
				if(!("SADM").equalsIgnoreCase(rolesMst.getUgRoleCode())) lst.add(rolesMst); //not to consider Super Admin Role for the Client, It is used only for internal BAAS purpose
			}
		}
		return lst;
	}

	@Override
	public List<UserRoleMapping> getUserRoleMappings(Integer legalEntityCode, String userId, String status) {
		

		Map<String , Object> properties=new HashMap<String, Object>();
		properties.put("legalEntityCode",legalEntityCode);
		properties.put("userId", userId);
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			List<UserRoleMapping> userRoleMappings=null;
			List<UserRoleMappingWrk> userRoleMappingWrks= maintUsergroupRolesDao.getEntitiesByMatchingProperties(UserRoleMappingWrk.class, properties);			
			if(userRoleMappingWrks!=null && !userRoleMappingWrks.isEmpty()){
				userRoleMappings=new ArrayList<UserRoleMapping>();
				UserRoleMapping userRoleMapping=null;
				for (UserRoleMappingWrk userRoleMappingWrk : userRoleMappingWrks) {
					userRoleMapping=new UserRoleMapping(); 
					BeanUtils.copyProperties(userRoleMappingWrk, userRoleMapping);
					userRoleMappings.add(userRoleMapping);
				}

			}
			return userRoleMappings;
		}else {
			return maintUsergroupRolesDao.getEntitiesByMatchingProperties(UserRoleMapping.class, properties);
		}
	}


	@Override
	public List<UserDeptMapping> getUserDeptMappings(Integer legalEntityCode, String userId, String status) {


		Map<String , Object> properties=new HashMap<String, Object>();
		properties.put("legalEntityCode",legalEntityCode);
		properties.put("userId", userId);
		if(!BankAuditConstant.STATUS_AUTH.equalsIgnoreCase(status)){
			List<UserDeptMapping> userDeptMappings=null;
			List<UserDeptMappingWrk> userDeptMappingWrks= userDeptMappingDao.getEntitiesByMatchingProperties(UserDeptMappingWrk.class, properties);
			if(userDeptMappingWrks!=null && !userDeptMappingWrks.isEmpty()){
				userDeptMappings=new ArrayList<UserDeptMapping>();
				UserDeptMapping userDeptMapping=null;
				for (UserDeptMappingWrk userDeptMappingWrk : userDeptMappingWrks) {
					userDeptMapping=new UserDeptMapping();
					BeanUtils.copyProperties(userDeptMappingWrk, userDeptMapping);
					userDeptMappings.add(userDeptMapping);
				}

			}
			return userDeptMappings;
		}else {
			return userDeptMappingDao.getEntitiesByMatchingProperties(UserDeptMapping.class, properties);
		}
	}

	@Override
	public void deleteUserRoleMapping(Integer legalEntityCode, String userId, String status) {

		maintUsergroupRolesDao.deleteUserRoleMapping( legalEntityCode,  userId,  status);
	}

	@Override
	public void deleteUserDeptMapping(Integer legalEntityCode, String userId, String status) {

		userDeptMappingDao.deleteUserDeptMapping( legalEntityCode,  userId,  status);
	}


}
