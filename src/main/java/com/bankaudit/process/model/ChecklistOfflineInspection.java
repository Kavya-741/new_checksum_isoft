package com.bankaudit.process.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="checklist_offline_inspection")
public class ChecklistOfflineInspection implements Serializable{

	private static final long serialVersionUID = 3834181442684683603L;

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="doc_id",  unique = false, nullable = false, insertable = true, updatable = false)
    private Integer docId;
	
	@Column(name="legal_entity_code")	
	private Integer legalEntityCode;
	
	@Column(name="observation_id")
	private String observationId;
	
	@Column(name="group_observation_id")
	private String groupObservationId;
	
	@Column(name="audit_id")
	private String auditId;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Column(name="mapping_id")
	private String mappingId;
	
	@Column(name="unit_code")
	private String unitCode;
	
	@Column(name="chapter")
	private String chapter;
	
	@Column(name="section")
	private String section;
	
	@Column(name="product")
	private String product;

	@Column(name="process")
	private String process;
	
	@Column(name="checklist")
	private String checklist;
	
	@Column(name="checklist_code")
	private String checklistCode;
		
	@Column(name="compliance_status")
	private String complianceStatus;
	
	@Column(name="non_compliance_persistence")
	private String nonCompliancePersistence;
	
	@Column(name="criticality")
	private String criticality;
	
	@Column(name="io_comments")
	private String ioComments;
	
	@Column(name="submission_status")
	private String submissionStatus;
	
	@Column(name="update_status")
	private String updateStatus;
	
	@Column(name="update_status_message")
	private String updateStatusMessage;
	
	@Column(name="response_status")
	private String responseStatus;
	
	@Column(name="review_status")
	private String reviewStatus;
	 
	@Column(name="review_status_attribute")
	private String reviewStatusAttribute;
	
	@Column(name="last_response_review_remarks")
	private String lastResponseReviewRemarks;
	
	@Column(name="response_review_remarks")
	private String responseReviewRemarks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public Integer getLegalEntityCode() {
		return legalEntityCode;
	}

	public void setLegalEntityCode(Integer legalEntityCode) {
		this.legalEntityCode = legalEntityCode;
	}

	public String getObservationId() {
		return observationId;
	}

	public void setObservationId(String observationId) {
		this.observationId = observationId;
	} 	

	public String getGroupObservationId() {
		return groupObservationId;
	}

	public void setGroupObservationId(String groupObservationId) {
		this.groupObservationId = groupObservationId;
	}

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getChecklist() {
		return checklist;
	}

	public void setChecklist(String checklist) {
		this.checklist = checklist;
	}

	public String getChecklistCode() {
		return checklistCode;
	}

	public void setChecklistCode(String checklistCode) {
		this.checklistCode = checklistCode;
	}

	public String getComplianceStatus() {
		return complianceStatus;
	}

	public void setComplianceStatus(String complianceStatus) {
		this.complianceStatus = complianceStatus;
	}

	public String getNonCompliancePersistence() {
		return nonCompliancePersistence;
	}

	public void setNonCompliancePersistence(String nonCompliancePersistence) {
		this.nonCompliancePersistence = nonCompliancePersistence;
	}

	public String getCriticality() {
		return criticality;
	}

	public void setCriticality(String criticality) {
		this.criticality = criticality;
	}

	public String getIoComments() {
		return ioComments;
	}

	public void setIoComments(String ioComments) {
		this.ioComments = ioComments;
	}

	public String getSubmissionStatus() {
		return submissionStatus;
	}

	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}

	public String getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getUpdateStatusMessage() {
		return updateStatusMessage;
	}

	public void setUpdateStatusMessage(String updateStatusMessage) {
		this.updateStatusMessage = updateStatusMessage;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getReviewStatusAttribute() {
		return reviewStatusAttribute;
	}

	public void setReviewStatusAttribute(String reviewStatusAttribute) {
		this.reviewStatusAttribute = reviewStatusAttribute;
	}

	public String getLastResponseReviewRemarks() {
		return lastResponseReviewRemarks;
	}

	public void setLastResponseReviewRemarks(String lastResponseReviewRemarks) {
		this.lastResponseReviewRemarks = lastResponseReviewRemarks;
	}

	public String getResponseReviewRemarks() {
		return responseReviewRemarks;
	}

	public void setResponseReviewRemarks(String responseReviewRemarks) {
		this.responseReviewRemarks = responseReviewRemarks;
	} 
	 
}
