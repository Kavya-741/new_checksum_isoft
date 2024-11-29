package com.bankaudit.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_audit_parameter_derived")
@Data
public class MaintAuditParameterDerived implements Serializable{
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="u_criticality")
	private String uCriticality;
	
	@Id
	@Column(name="unit_type")
	private String unitType;
	
	@Id
	@Column(name="classification")
	private String classification;
	
	@Id
	@Column(name="rating")
	private String rating;
	
	@Id
	@Column(name="revenue_leakage")
	private String revenueLeakage;	
	
	/*@Column(name="frequency_type")
	private String frequencyType;*/
	
	@Column(name="frequency")
	private String frequency;
	
	@Column(name="duration")
	private String duration;
	
	@Column(name="man_days")
	private String manDays;
		
	@Column(name="response_time", nullable = true)
	private String responseTime;
	
	@Column(name="closure_time")
	private String closureTime;
	
	private transient String auditTypeName;
	
}
