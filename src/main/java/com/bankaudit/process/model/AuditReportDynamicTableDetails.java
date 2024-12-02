package com.bankaudit.process.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="audit_report_dynamic_table_details")
@Data
public class AuditReportDynamicTableDetails implements Serializable{
 
	private static final long serialVersionUID = 6409894910839262557L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Integer id;

	@Column(name="legal_entity_code")
	private Integer legalEntityCode;

	@Column(name="audit_type_code")
	private String auditTypeCode;

	@Column(name="audit_id")
	private String auditId;
 
	//@JoinColumn(name="id")
	@Column(name="table_id", unique = false, nullable = false, insertable = true, updatable = false)  
	private Integer tableId;
	
	@Column(name="sequence_row")
	private Integer sequenceRow;
	
	@Column(name="content01")
	private String content01;

	@Column(name="content02")
	private String content02;

	@Column(name="content03")
	private String content03;

	@Column(name="content04")
	private String content04;

	@Column(name="content05")
	private String content05;

	@Column(name="content06")
	private String content06;

	@Column(name="content07")
	private String content07;

	@Column(name="content08")
	private String content08;

	@Column(name="content09")
	private String content09;
	
	
	private transient String rowStatus;

}
