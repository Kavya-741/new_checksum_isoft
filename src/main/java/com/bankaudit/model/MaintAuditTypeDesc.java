package com.bankaudit.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MaintAuditTypeDescId.class)
@Table(name = "maint_audit_type_desc")
public class MaintAuditTypeDesc implements Serializable {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Column(name = "audit_type_desc")
	private String auditTypeDesc;

	@Id
	@Column(name = "audit_type_code")
	private String auditTypeCode;

	@Column(name = "audit_category")
	private String auditCategory;

	@Column(name = "transaction_allowed")
	private String transactionAllowed;

	@Column(name = "io_flexibility")
	private String ioFlexibility;

	@Column(name = "respond_submission_chk")
	private String respondSubmissionChk;

	@Column(name = "closure_request_chk")
	private String closureRequestChk;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "status")
	private String status;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

}
