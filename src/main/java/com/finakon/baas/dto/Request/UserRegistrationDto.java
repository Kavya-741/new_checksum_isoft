package com.finakon.baas.dto.Request;

import java.util.List;
import java.util.Set;

import com.finakon.baas.entities.MaintEntityAuditSubgroupMappingWrk;
import com.finakon.baas.entities.UserWrk;

import lombok.Data;

@Data
public class UserRegistrationDto {

	UserWrk userWrk;
	Set<String> userRoleIds;
	List<MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappings;
	List<String> userDeptIds;

}
