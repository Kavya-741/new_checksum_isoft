package com.bankaudit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_criticality_wrk")
@Data
public class MaintCriticalityWrk implements  Serializable{


	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="criticality_code")
	private String criticalityCode;
	
	@Id
	@Column(name="criticality_of_type")
	private String criticalityOfType;
	
	@Column(name="criticality_desc")
	private String criticalityDesc;
	
	@Column(name="score")
	private String score;
	
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
