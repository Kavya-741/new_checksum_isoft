package com.finakon.baas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_role_mapping")
public class UserRoleMapping {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Column(name = "user_id")
	private String userId;


	@Column(name = "user_role_id")
	private String userRoleId;

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

	@Column(name = "businessdate_timestamp")
	private Date businessdateTimestamp;

	@Column(name = "sysdate_timestamp")
	private Date sysdateTimestamp;

}
