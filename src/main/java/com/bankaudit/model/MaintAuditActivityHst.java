package com.bankaudit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_audit_activity_hst")
@Data
public class MaintAuditActivityHst {

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Column(name="activity_id")
	private String activityId;
	
	@Column(name="activity_code")
	private String activityCode;
	
	@Column(name="activity_name")
	private String activityName;
	
	@Column(name="activity_name_hindi")
	private String activityNameHindi;
	
	@Column(name="a_criticality")
	private String aCriticality;
	
	@Column(name = "auth_rej_remarks")
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
