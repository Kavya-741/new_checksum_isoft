package com.bankaudit.process.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "audit_document_details")
@Data
public class AuditDocumentDetails implements Serializable {

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Column(name = "audit_type_code")
	private String auditTypeCode;

	@Column(name = "plan_id")
	private String planId;

	@Column(name = "audit_id")
	private String auditId;

	@Column(name = "observation_id")
	private String observationId;

	@Column(name = "group_observation_id")
	private String groupObservationId;

	@Column(name = "transaction_account_field01")
	private String transactionAccountField01;

	@Column(name = "transaction_account_field02")
	private String transactionAccountField02;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "related_entity_type")
	private String relatedEntityType;

	@Column(name = "related_entity_id")
	private String relatedEntityId;

	@Column(name = "system_id")
	private String systemId;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "reference_path")
	private String referencePath;

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

	@Column(name = "role")
	private String role;

	@Column(name = "document_name")
	private String documentName;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "document_sub_type")
	private String documentSubType;

	@Column(name = "capture_type")
	private String captureType;

	@Column(name = "auth_rej_remarks")
	private String authRejRemarks;

}
