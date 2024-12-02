package com.bankaudit.process.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ifc_parameter_mapping")
public class IfcParameterMapping implements Serializable {
	
	@Id
	@Column(name="id")
	private Integer id;

	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Column(name="parameter_name_nabard")
	private String parameterNameNabard;
	
	@Column(name="parameter_name_baas")
	private String parameterNameBaas;
	
	@Column(name="parameter_description")
	private String parameterDescription;

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

	public String getParameterDescription() {
		return parameterDescription;
	}

	public void setParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

}
