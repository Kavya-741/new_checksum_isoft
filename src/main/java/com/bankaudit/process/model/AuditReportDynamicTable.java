package com.bankaudit.process.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="audit_report_dynamic_table")
@Data
public class AuditReportDynamicTable implements Serializable {
	 
	private static final long serialVersionUID = 533235104198637355L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Integer id;

	@Column(name="legal_entity_code")
	private Integer legalEntityCode;

	@Column(name="audit_type_code")
	private String auditTypeCode;

	@Column(name="plan_id")
	private String planId;

	@Column(name="audit_id")
	private String auditId;

	@Column(name="unit_id")
	private String unitId;
	
	@Column(name="statement_id")
	private String statementId;	 
	
	@Column(name="audit_group_code")
	private String auditGroupCode;
		
	@Column(name="audit_sub_group_code")
	private String auditSubGroupCode;	 
	
	@Column(name="activity_code")
	private String activityCode;
	
	@Column(name="process_code")
	private String processCode;
	
	@Column(name="finding_code")
	private String findingCode;
	 
	@Column(name="applicable_for")
	private String applicableFor;
	
	@Column(name="statement_type")
	private String statementType;
	 
	@Column(name="sequence")
	private Integer sequence;

	@Column(name="table_name")
	private String tableName;
	
	@Column(name="template_name")
	private String templateName;
	
	@Column(name="remarks")
	private String remarks;

	@Column(name="maker")
	private String maker;

	@Column(name="maker_timestamp")
	private Date makerTimestamp;

	@Column(name="header01")
	private String header01;

	@Column(name="header02")
	private String header02;

	@Column(name="header03")
	private String header03;

	@Column(name="header04")
	private String header04;

	@Column(name="header05")
	private String header05;

	@Column(name="header06")
	private String header06;

	@Column(name="header07")
	private String header07;

	@Column(name="header08")
	private String header08;

	@Column(name="header09")
	private String header09;
	
	@Column(name="no_of_columns")
	private Integer noOfColumns;
	 
	//Primary Table AuditLevelDocumentDetails
	@Column(name="doc_id",  unique = false, /*nullable = false,*/ insertable = true, updatable = false)
    private Integer docId;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)   
	@JoinColumn(name = "table_id")
	private List<AuditReportDynamicTableDetails> auditReportDynamicTableDetailList;  
	 	
	private transient String unitName;
	private transient String auditStatus;
	private transient String auditStatusDesc;
	
	private transient String role;
	private transient String documentType;
	private transient String documentSubType;
	private transient String warningCheck; 
	private transient String documentName;
	private transient String referencePath;
	private transient String observationStatus;
	private transient String fileName;
	
}
