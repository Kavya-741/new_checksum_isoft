package com.bankaudit.process.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ifc_ensure_baas_data")
public class IfcEnsureBaasData implements Serializable {
	
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
	
	@Column(name="stage_of_file")
	private String stageOfFile;

	@Column(name="reference_no")
	private Integer referenceNo;
	
	@Column(name="parameter_name")
	private String parameterName;

	@Column(name="parameter_name_nabard")
	private String parameterNameNabard;
	
	@Column(name="parameter_name_baas")
	private String parameterNameBaas;

	@Column(name="unit_code")
	private String unitCode;

	@Column(name="unit_name")
	private String unitName;

	@Column(name="unit_type")
	private String unitType;

	@Column(name="period_end_date_latest")
	private String periodEndDateLatest;

	@Column(name="submitted_value_latest")
	private String submittedValueLatest;
	
	@Column(name="period_end_date_middest")
	private String periodEndDateMiddest;

	@Column(name="submitted_value_middest")
	private String submittedValueMiddest;

	@Column(name="period_end_date_oldest")
	private String periodEndDateOldest;

	@Column(name="submitted_value_oldest")
	private String submittedValueOldest;
	
	@Column(name="irefresh_flag")
	private String irefreshFlag;
	
	@Column(name="maker")
	private String maker;
	
	@Column(name="maker_timestamp")
	private Date makerTimestamp;

	private transient String parameterDescription;
	
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

	public String getStageOfFile() {
		return stageOfFile;
	}

	public void setStageOfFile(String stageOfFile) {
		this.stageOfFile = stageOfFile;
	}

	public Integer getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(Integer referenceNo) {
		this.referenceNo = referenceNo;
	}
	
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterNameNabard() {
		return parameterNameNabard;
	}

	public void setParameterNameNabard(String parameterNameNabard) {
		this.parameterNameNabard = parameterNameNabard;
	}

	public String getParameterNameBaas() {
		return parameterNameBaas;
	}

	public void setParameterNameBaas(String parameterNameBaas) {
		this.parameterNameBaas = parameterNameBaas;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getPeriodEndDateLatest() {
		return periodEndDateLatest;
	}

	public void setPeriodEndDateLatest(String periodEndDateLatest) {
		this.periodEndDateLatest = periodEndDateLatest;
	}

	public String getSubmittedValueLatest() {
		return submittedValueLatest;
	}

	public void setSubmittedValueLatest(String submittedValueLatest) {
		this.submittedValueLatest = submittedValueLatest;
	}

	public String getPeriodEndDateMiddest() {
		return periodEndDateMiddest;
	}

	public void setPeriodEndDateMiddest(String periodEndDateMiddest) {
		this.periodEndDateMiddest = periodEndDateMiddest;
	}

	public String getSubmittedValueMiddest() {
		return submittedValueMiddest;
	}

	public void setSubmittedValueMiddest(String submittedValueMiddest) {
		this.submittedValueMiddest = submittedValueMiddest;
	}

	public String getPeriodEndDateOldest() {
		return periodEndDateOldest;
	}

	public void setPeriodEndDateOldest(String periodEndDateOldest) {
		this.periodEndDateOldest = periodEndDateOldest;
	}

	public String getSubmittedValueOldest() {
		return submittedValueOldest;
	}

	public void setSubmittedValueOldest(String submittedValueOldest) {
		this.submittedValueOldest = submittedValueOldest;
	}

	public String getIrefreshFlag() {
		return irefreshFlag;
	}

	public void setIrefreshFlag(String irefreshFlag) {
		this.irefreshFlag = irefreshFlag;
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

	public String getParameterDescription() {
		return parameterDescription;
	}

	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}


}
