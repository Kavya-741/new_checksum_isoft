package com.bankaudit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_audit_process")
@Data
public class MaintAuditProcess implements Serializable  {
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	

	@Id
	@Column(name="process_id")
	private String processId;
	
	
	@Column(name="process_code")
	private String processCode;
	

	@Column(name="process_name")
	private String processName;
	
	@Column(name="process_name_hindi")
	private String processNameHindi;
	
	@Column(name="p_criticality")
	private String pCriticality;
	
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
