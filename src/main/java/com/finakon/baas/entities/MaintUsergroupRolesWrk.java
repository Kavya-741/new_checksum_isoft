package com.finakon.baas.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.finakon.baas.entities.IdClasses.MaintUserGroupRolesId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "maint_usergroup_roles_wrk")
@IdClass(MaintUserGroupRolesId.class)
public class MaintUsergroupRolesWrk {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Id
	@Column(name = "ug_role_code")
	private String ugRoleCode;

	@Column(name = "ug_role_name")
	private String ugRoleName;

	@Column(name = "ug_role_desc")
	private String ugRoleDesc;

	@Column(name = "auth_rej_remarks")
	private String authRejRemarks;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "status")
	private String status;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

}
