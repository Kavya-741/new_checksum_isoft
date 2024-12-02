package com.bankaudit.process.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="audit_team_wrk")
@Data
public class AuditTeamWrk implements Serializable {
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="plan_id")
	private String planId;
	
	@Column(name="unit_id")
	private String unitId;
	
	
	@Id
	@Column(name="audit_id")
	private String auditId;
	
	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(name="user_type")
	private String userType;
}
