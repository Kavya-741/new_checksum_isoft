package com.bankaudit.process.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="audit_team")
public class AuditTeam implements Serializable {
	

	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="plan_id")
	private String planId;
	
	@Column(name="unit_id")
	private String unitId;
	
	
	@Id
	@Column(name="audit_id")
	private String auditId;
	
	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(name="user_type")
	private String userType;

	public AuditTeam() {
	}

	
	/**
	 * @param legalEntityCode
	 * @param auditTypeCode
	 * @param planId
	 * @param unitId
	 * @param auditId
	 * @param userId
	 * @param userType
	 */
	public AuditTeam(AuditTeamWrk auditTeamWrk) {
		super();
		this.legalEntityCode = auditTeamWrk.getLegalEntityCode();
		this.auditTypeCode = auditTeamWrk.getAuditTypeCode();
		this.planId = auditTeamWrk.getPlanId();
		this.unitId = auditTeamWrk.getUnitId();
		this.auditId = auditTeamWrk.getAuditId();
		this.userId = auditTeamWrk.getUserId();
		this.userType = auditTeamWrk.getUserType();
	}


	/**
	 * @return the auditId
	 */
	public String getAuditId() {
		return auditId;
	}

	/**
	 * @param auditId the auditId to set
	 */
	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	/**
	 * @return the legalEntityCode
	 */
	public Integer getLegalEntityCode() {
		return legalEntityCode;
	}

	/**
	 * @param legalEntityCode the legalEntityCode to set
	 */
	public void setLegalEntityCode(Integer legalEntityCode) {
		this.legalEntityCode = legalEntityCode;
	}



	/**
	 * @return the auditTypeCode
	 */
	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	/**
	 * @param auditTypeCode the auditTypeCode to set
	 */
	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	/**
	 * @return the planId
	 */
	public String getPlanId() {
		return planId;
	}

	/**
	 * @param planId the planId to set
	 */
	public void setPlanId(String planId) {
		this.planId = planId;
	}

	/**
	 * @return the unitId
	 */
	public String getUnitId() {
		return unitId;
	}

	/**
	 * @param unitId the unitId to set
	 */
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	
}
