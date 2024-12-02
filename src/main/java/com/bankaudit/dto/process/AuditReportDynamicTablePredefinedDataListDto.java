package com.bankaudit.dto.process;

public class AuditReportDynamicTablePredefinedDataListDto {
	
	private Integer legalEntityCode; 
	private String auditTypeCode; 
	private String auditId;	
	private String statementId;	
	
	private String auditGroupCode; 
	private String auditSubGroupCode;	 
	private String activityCode; 
	private String processCode; 
	private String findingCode; 
	
	private String applicableFor; 
	private String statementType; 	
	private String statement; 
	private Integer reportSequence;
	private Integer maintReportSequence;
	
	private String recordStatus;
	
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
	public String getStatementId() {
		return statementId;
	}
	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}
	public String getAuditGroupCode() {
		return auditGroupCode;
	}
	public void setAuditGroupCode(String auditGroupCode) {
		this.auditGroupCode = auditGroupCode;
	}
	public String getAuditSubGroupCode() {
		return auditSubGroupCode;
	}
	public void setAuditSubGroupCode(String auditSubGroupCode) {
		this.auditSubGroupCode = auditSubGroupCode;
	}
	public String getActivityCode() {
		return activityCode;
	}
	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}
	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}
	public String getFindingCode() {
		return findingCode;
	}
	public void setFindingCode(String findingCode) {
		this.findingCode = findingCode;
	}
	public String getApplicableFor() {
		return applicableFor;
	}
	public void setApplicableFor(String applicableFor) {
		this.applicableFor = applicableFor;
	}
	public String getStatementType() {
		return statementType;
	}
	public void setStatementType(String statementType) {
		this.statementType = statementType;
	}
	public String getStatement() {
		return statement;
	}
	public void setStatement(String statement) {
		this.statement = statement;
	}	 
	public Integer getReportSequence() {
		return reportSequence;
	}
	public void setReportSequence(Integer reportSequence) {
		this.reportSequence = reportSequence;
	}
	public Integer getMaintReportSequence() {
		return maintReportSequence;
	}
	public void setMaintReportSequence(Integer maintReportSequence) {
		this.maintReportSequence = maintReportSequence;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	} 
}
