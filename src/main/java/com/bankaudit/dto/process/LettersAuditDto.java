package com.bankaudit.dto.process;

public class LettersAuditDto {

	private String legalEntityCode;
	private String financialYear;
	private String auditTypeCode;
	private String auditId;
	private String planId;
	private String unitName;  
	private String parentUnitName;
  
	private String actualStartDate;
	private String exitDate; //reportDate
	private String dispatchDate; //reportDispatchDate
	private String auditStatus;
	private String observationStatus;
	private String reportStatus;
	private String complianceStatus;
	private String scrutinyStatus;
	private String letterName;
	private String letterStatus;
	
	private String pio;
	private String aio;
	
	public String getLegalEntityCode() {
		return legalEntityCode;
	}
	public void setLegalEntityCode(String legalEntityCode) {
		this.legalEntityCode = legalEntityCode;
	}
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getParentUnitName() {
		return parentUnitName;
	}
	public void setParentUnitName(String parentUnitName) {
		this.parentUnitName = parentUnitName;
	}
	public String getActualStartDate() {
		return actualStartDate;
	}
	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}
	public String getExitDate() {
		return exitDate;
	}
	public void setExitDate(String exitDate) {
		this.exitDate = exitDate;
	}
	public String getDispatchDate() {
		return dispatchDate;
	}
	public void setDispatchDate(String dispatchDate) {
		this.dispatchDate = dispatchDate;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getObservationStatus() {
		return observationStatus;
	}
	public void setObservationStatus(String observationStatus) {
		this.observationStatus = observationStatus;
	}
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	public String getComplianceStatus() {
		return complianceStatus;
	}
	public void setComplianceStatus(String complianceStatus) {
		this.complianceStatus = complianceStatus;
	}
	public String getScrutinyStatus() {
		return scrutinyStatus;
	}
	public void setScrutinyStatus(String scrutinyStatus) {
		this.scrutinyStatus = scrutinyStatus;
	}
	public String getLetterName() {
		return letterName;
	}
	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}
	public String getLetterStatus() {
		return letterStatus;
	}
	public void setLetterStatus(String letterStatus) {
		this.letterStatus = letterStatus;
	}
	public String getPio() {
		return pio;
	}
	public void setPio(String pio) {
		this.pio = pio;
	}
	public String getAio() {
		return aio;
	}
	public void setAio(String aio) {
		this.aio = aio;
	}
 
}
