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
@Table(name = "maint_report_standard_stmt")
@Data
public class MaintReportStandardStmt implements Serializable {

	private static final long serialVersionUID = 239382048776514056L;


	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Column(name = "mapping_id")
	private String mappingId;

	@Column(name = "audit_type_code")
	private String auditTypeCode;

	@Column(name = "report_id")
	private String reportId;

	@Column(name = "standard_stmnt_non_compliance")
	private String standardStmntNonCompliance;

	@Column(name = "standard_stmnt_compliance")
	private String standardStmntCompliance;

	@Column(name = "reference")
	private String reference;

	@Column(name = "standard_stmnt_non_compliance_print")
	private String standardStmntNonCompliancePrint;

	@Column(name = "standard_stmnt_compliance_print")
	private String standardStmntCompliancePrint;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "status")
	private String status;

	@Column(name = "maker")
	private String maker;

	@Column(name = "auth_rej_remarks")
	private String authRejRemarks;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

	private transient String auditGroupCode;
	private transient String auditSubGroupCode;
	private transient String activityCode;
	private transient String processCode;
	private transient String findingCode;

}
