package com.bankaudit.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="maint_entity_audit_subgroup_mapping_hst")
@Data
public class MaintEntityAuditSubgroupMappingHst implements Serializable {
	
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name="id1")
	private String id1;
	
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;

	@Column(name="mapping_type")
	private String mappingType;
	
	@Column(name="id")
	private String id;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Column(name="audit_group_code")
	private String auditGroupCode;
	
	@Column(name="audit_sub_group_code")
	private String auditSubGroupCode;
	
	
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
