package com.finakon.baas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import com.finakon.baas.entities.IdClasses.UserRoleMappingId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_role_mapping_wrk")
@IdClass(UserRoleMappingId.class)
public class UserRoleMappingWrk {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Id
	@Column(name = "user_id")
	private String userId;

	@Id
	@Column(name = "user_role_id")
	private String userRoleId;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "user_role_id", referencedColumnName = "ug_role_code", insertable = false, updatable = false),
        @JoinColumn(name = "legal_entity_code", referencedColumnName = "legal_entity_code", insertable = false, updatable = false)
    })
    private MaintUsergroupRoles maintUsergroupRoles;
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
	private String checkerTimestamp;

}
