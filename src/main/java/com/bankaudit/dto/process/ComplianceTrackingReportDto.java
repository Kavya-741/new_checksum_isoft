package com.bankaudit.dto.process;

public class ComplianceTrackingReportDto {

	private String legalEntityCode;
	private String financialYear;
	private String auditTypeCode;
	private String unitName;
	private String parentUnitName;
	private String chapter;
	private String section;
	private String product;
	private String process;
	private String checkList;
	private String complianceStatus;
	private String complianceStatusDesc;
	private String mappingId;

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

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
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

	public String getCheckList() {
		return checkList;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	public String getComplianceStatus() {
		return complianceStatus;
	}

	public void setComplianceStatus(String complianceStatus) {
		this.complianceStatus = complianceStatus;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getParentUnitName() {
		return parentUnitName;
	}

	public void setParentUnitName(String parentUnitName) {
		this.parentUnitName = parentUnitName;
	}

	public String getComplianceStatusDesc() {
		return complianceStatusDesc;
	}

	public void setComplianceStatusDesc(String complianceStatusDesc) {
		this.complianceStatusDesc = complianceStatusDesc;
	}

}
