package com.bankaudit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_audit_parameter_wrk")
@Data
public class MaintAuditParameterWrk implements Serializable{ 
	

	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	//@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Integer id;
	
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(updatable=false,insertable=false, name="legal_entity_code", referencedColumnName="legal_entity_code"),
        @JoinColumn(updatable=false,insertable=false, name="audit_type_code", referencedColumnName="audit_type_code")        
    })
	private MaintAuditTypeDesc maintAuditTypeDesc;
		
	@Column(name="u_criticality", nullable = true)
	private String uCriticality;
	
	@Column(name="unit_type", nullable = true)
	private String unitType;
	
	@Column(name="classification", nullable = true)
	private String classification;
	
	@Column(name="frequency_type")
	private String frequencyType;
	
	@Column(name="frequency")
	private String frequency;
	
	@Column(name="duration")
	private String duration;
	
	@Column(name="man_days")
	private String manDays;
		
	@Column(name="response_time", nullable = true)
	private String responseTime;
	
	@Column(name="rating", nullable = true)
	private String rating;
	
	@Column(name="revenue_leakage", nullable = true)
	private String revenueLeakage;	
	
	@Column(name="closure_time")
	private String closureTime;
	
	@Column(name = "entity_status")
	private String entityStatus;
	
	@Column(name="status")
	private String status;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;	
				
	@Column(name="maker")
	private String maker;
	
	@Column(name="maker_timestamp")
	private Date makerTimestamp;
	
	@Column(name="checker")
	private String checker;
	
	@Column(name="checker_timestamp")
	private Date checkerTimestamp;

}
