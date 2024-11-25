package com.bankaudit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_audit_finding_wrk")
@Data
public class MaintAuditFindingWrk implements Serializable {
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="finding_id")
	private String findingId;	
	
	@Column(name="finding_code")
	private String findingCode;	

	@Column(name="finding_name")
	private String findingName;
	
	@Column(name="finding_name_hindi")
	private String findingNameHindi;
	
	@Column(name="f_criticality")
	private String fCriticality;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;
	
	@Column(name = "entity_status")
	private String entityStatus;
	
	@Column(name="status")
	private String status;
	
	@Column(name="maker")
	private String maker;
	
	@Column(name="maker_timestamp")
	private Date makerTimestamp;
	
	@Column(name="checker")
	private String checker;
	
	@Column(name="checker_timestamp")
	private Date checkerTimestamp;
	
}
