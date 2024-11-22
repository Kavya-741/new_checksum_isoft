package com.bankaudit.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_role_mapping")
@IdClass(UserRoleMappingId.class)
public class UserRoleMapping implements Serializable {

	@Id
    @Column(name = "legal_entity_code", nullable = false)
    private Integer legalEntityCode;

    @Id
    @Column(name = "user_role_id", nullable = false)
    private String userRoleId;

	@Id
    @Column(name = "user_id", nullable = false)
    private String userId;

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
