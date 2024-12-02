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

@Entity
@Table(name = "audit_level_document_details")
public class AuditLevelDocumentDetails implements Serializable {
 
	private static final long serialVersionUID = -4853589636885476933L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Column(name = "audit_type_code")
	private String auditTypeCode;

	@Column(name = "audit_id")
	private String auditId;
	
	@Column(name = "plan_id")
	private String planId;
	
	@Column(name = "document_name")
	private String documentName;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "document_sub_type")
	private String documentSubType;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "reference_no")
	private Integer referenceNo;

	@Column(name = "reference_path")
	private String referencePath;

	@Column(name = "unit_code")
	private String unitCode;

	@Column(name = "user_role_id")
	private String userRoleId;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "upload_status")
	private String uploadStatus;
	
	@Column(name = "seq_id")
	private String seqId;	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER  )   
	@JoinColumn(name = "doc_id")
	private List<ChecklistOfflineInspection> checklistOfflineInspection;  
	 
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLegalEntityCode() {
		return legalEntityCode;
	}

	public void setLegalEntityCode(Integer legalEntityCode) {
		this.legalEntityCode = legalEntityCode;
	}

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentSubType() {
		return documentSubType;
	}

	public void setDocumentSubType(String documentSubType) {
		this.documentSubType = documentSubType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(Integer referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getReferencePath() {
		return referencePath;
	}

	public void setReferencePath(String referencePath) {
		this.referencePath = referencePath;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public Date getMakerTimestamp() {
		return makerTimestamp;
	}

	public void setMakerTimestamp(Date makerTimestamp) {
		this.makerTimestamp = makerTimestamp;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public List<ChecklistOfflineInspection> getChecklistOfflineInspection() {
		return checklistOfflineInspection;
	}

	public void setChecklistOfflineInspection(List<ChecklistOfflineInspection> checklistOfflineInspection) {
		this.checklistOfflineInspection = checklistOfflineInspection;
	}

	 

	 
	
	 
}
