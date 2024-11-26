package com.bankaudit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="activity_process_finding_mapping_hst")
public class ActivityProcessFindingMappingHst {
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	// @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY) 
	@Column(name="mapping_id")
	private String mappingId;
	
	@Column(name="mapping_code")
	private String mappingCode;
	
	@Column(name="activity_code")
	private String activityCode;
	@Column(name="process_code")
	private String processCode;
	@Column(name="finding_code")
	private String findingCode;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
		
	@Column(name="audit_group_code")
	private String auditGroupCode;
		
	@Column(name="audit_sub_group_code")
	private String auditSubGroupCode;
		
	@Column(name="activity_id")
	private String activityId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(
            name = "legal_entity_code",
            referencedColumnName = "legal_entity_code",insertable=false,updatable=false),
        @JoinColumn(
            name = "activity_id",
            referencedColumnName = "activity_id",insertable=false,updatable=false)
    })
	MaintAuditActivityHst maintAuditActivityHst;
	
	@Column(name="process_id")
	private String processId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(
            name = "legal_entity_code",
            referencedColumnName = "legal_entity_code",insertable=false,updatable=false),
        @JoinColumn(
            name = "process_id",
            referencedColumnName = "process_id",insertable=false,updatable=false)
    })
	MaintAuditProcessHst maintAuditProcessHst;
	
	
	@Column(name="finding_id")
	private String findingId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(
            name = "legal_entity_code",
            referencedColumnName = "legal_entity_code",insertable=false,updatable=false),
        @JoinColumn(
            name = "finding_id",
            referencedColumnName = "finding_id",insertable=false,updatable=false)
    })
	MaintAuditFindingHst maintAuditFindingHst;
	
	@Column(name="risk_belongs_to")
	private String riskBelongsTo;
	
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
