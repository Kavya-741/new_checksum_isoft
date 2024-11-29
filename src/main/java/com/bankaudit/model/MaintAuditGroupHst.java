package com.bankaudit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="maint_audit_group_hst")
public class MaintAuditGroupHst {
	
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Column(name="audit_group_code")
	private String auditGroupCode;
	

	@Column(name="audit_group_name")
	private String auditGroupName;
	
	@Column(name="audit_group_name_hindi")
	private String auditGroupNameHindi;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;
	
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
