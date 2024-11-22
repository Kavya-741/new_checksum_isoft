package com.bankaudit.dto;

import java.util.List;
import java.util.Set;

import com.bankaudit.model.MaintEntityAuditSubgroupMappingWrk;
import com.bankaudit.model.UserWrk;

import lombok.Data;

@Data
public class UserRegistrationDto {

	UserWrk userWrk;
	Set<String> userRoleIds;
	List<MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappings;
	List<String> userDeptIds;

}
