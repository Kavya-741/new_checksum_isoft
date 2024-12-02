package com.bankaudit.process.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;

@Entity
@Table(name="audit_schedule")
@Data
public class AuditSchedule implements Serializable{

	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="plan_id")
	private String planId;
	
	@Id
	@Column(name="audit_id")
	private String auditId;
	
	@Column(name="unit_id")
	private String unitId;
	
	@Column(name="unit_head")
	private String unitHead;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="last_audit_date")
	private Date lastAuditDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="schedule_start_date")
	private Date scheduleStartDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="schedule_end_date")
	private Date scheduleEndDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="actual_start_date")
	private Date actualStartDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="actual_end_date")
	private Date actualEndDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="report_date")
	private Date reportDate;
	
	@Column(name="audit_status")
	private String auditStatus;
	
	@Column(name="observation_status")
	private String observationStatus;
	
	@Column(name="respond_status")
	private String respondStatus;
	
	@Column(name="review_status")
	private String reviewStatus;
	
	@Column(name="respond_closure_status")
	private String respondClosureStatus;
	
	@Column(name="total_findings")
	private Integer totalFindings;
	
	@Column(name="open_findings")
	private Integer openFindings;
	
	@Column(name="rectified_findings")
	private Integer rectifiedFindings;
	
	@Column(name="closed_findings")
	private Integer closedFindings;
	
	@Column(name="responded_findings")
	private Integer respondedFindings;
	
	@Column(name="response_time")
	private Integer responseTime;
	
	@Column(name="closure_time")
	private String closureTime;
	
	@Column(name="revenue_leakage")
	private Integer revenueLeakage;
	
	@Column(name="rating_id")
	private String ratingId;
	
	@Column(name="assessed_risk_prcntg")
	private String assessedRiskPrcntg;

	@Column(name="assessed_rating")
	private String assessedRating;

	@Column(name="effective_risk_prcntg")
	private String effectiveRiskPrcntg;

	@Column(name="effective_rating")
	private String effectiveRating;

	@Column(name="rating_override_remarks")
	private String ratingOverrideRemarks;
	
	@Column(name="auditer_id")
	private String auditerId;
	
	@Column(name="responder_id")
	private String responderId;
	
	@Column(name="reviewer_id")
	private String reviewerId;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="responded_date")
	private Date respondedDate;
	
	@Column(name="respond_closure_date")
	private Date respondClosureDate;	
	
	@Column(name="respond_initiate_date")
	private Date respondInitiatedDate;
	
	//@Temporal(TemporalType.DATE)
	@Column(name="reviewed_date")
	private Date reviewedDate;
	
	@Column(name="review_initiated_date")
	private Date reviewInitiatedDate;
	
	@Column(name="closure_user_id")
	private String closureUserId;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="status")
	private String status;
	
	@Column(name="maker")
	private String maker;
	
	@Column(name="maker_timestamp")
	private Date makerTimestamp;
	
	@Column(name="checker")
	private String checker;
	
	@Column(name="checker_timestamp")
	private Date checkerTimestamp;
	
	@Column(name="audit_consider_for_planning")
	private String auditConsiderForPlanning;
	
	@Column(name="prev_audit_id")
	private String prevAuditId; 
	
	@Column(name="prev_assessed_risk_prcntg")
	private String prevAssessedRiskPrcntg;
		
	@Column(name="rating_improvement")
	private String ratingImprovement; 
	
	@Column(name="frequency")
	private String frequency; 
	
	@Column(name="duration")
	private String duration;
		
	@Column(name="man_days")
	private String manDays; 
	
	@Column(name="planned_audit_continue_drop")
	private String plannedAuditContinueDrop;
	
	@Column(name="plan_type",nullable=true)
	private String planType;
	
	@Column(name="plan_comments")
	private String planComments; 
	
	@Column(name="plan_audit_remarks")
	private String planAuditRemarks; 
	
	@Column(name="plan_validation_mismatch")
	private String planValidationMismatch;
	
	@Column(name="audit_category")
	private String auditCategory;
	
	@Column(name="transaction_allowed")
	private String transactionAllowed;
	
	@Column(name="io_flexibility")
	private String ioFlexibility;	
	
	@Column(name="interface_acc_fetch")
	private String interfaceAccFetch;
	
	/****************** Columns Related to NABARD Assessment ******************/
	@Column(name="inhrt_risk_code")
	private String inhrtRiskCode;

	@Column(name="inhrt_risk_desc")
	private String inhrtRiskDesc;

	@Column(name="inhrt_risk_score")
	private String inhrtRiskScore;

	@Column(name="cntrl_cls_bucket_code")
	private String cntrlClsBucketCode;

	@Column(name="cntrl_cls_bucket_desc")
	private String cntrlClsBucketDesc;

	@Column(name="cntrl_cls_bucket_score")
	private String cntrlClsBucketScore;

	@Column(name="residul_risk_code")
	private String residulRiskCode;

	@Column(name="residul_risk_desc")
	private String residulRiskDesc;

	@Column(name="residul_risk_score")
	private String residulRiskScore;

	@Column(name="risk_weightage_percentage")
	private String riskWeightagePercentage;

	@Column(name="total_no_risk")
	private String totalNoRisk;

	@Column(name="total_weightage")
	private String totalWeightage;

	@Column(name="risk_index_score")
	private String riskIndexScore;

	@Column(name="control_profile_code")
	private String controlProfileCode;

	@Column(name="control_profile_desc")
	private String controlProfileDesc;

	@Column(name="control_profile_score")
	private String controlProfileScore;

	@Column(name="health_index_code")
	private String healthIndexCode;

	@Column(name="health_index_dsec")
	private String healthIndexDsec;

	@Column(name="health_index_score")
	private String healthIndexScore;
	
	@Column(name="team_maker")
	private String teamMaker;

	@Column(name="team_maker_timestamp")
	private Date teamMakerTimestamp;
	
	@Column(name="team_checker")
	private String teamChecker;

	@Column(name="team_checker_timestamp")
	private Date teamCheckerTimestamp;
	
	@Column(name="team_status")
	private String teamStatus;
	
	@Column(name="team_auth_rej_remarks")
	private String teamAuthRejRemarks;	
		
	/*************** End Here, NABARD Assessment ******************/
	
	/*************** Columns related to BaaSDOS, Report related *********************/
	@Column(name="report_status")
	private String reportStatus;
	
	@Column(name="allocator")
	private String allocator;
	
	@Column(name="report_reviewer")
	private String reportReviewer;
	
	@Column(name="report_reviewer_start_date")
	private Date reportReviewerStartDate;
	
	@Column(name="report_reviewer_target_date")
	private Date reportReviewerTargetDate;
	
	@Column(name="report_target_date")
	private Date reportTargetDate;
	
	@Column(name="report_review_end_date")
	private Date reportReviewEndDate;
	
	@Column(name="report_dispatch_date")
	private Date reportDispatchDate;	
	
	@Column(name="ir_submission_date")
	private Date irSubmissionDate;
	
	@Column(name="ir_finalisation_date")
	private Date irFinalisationDate;
	
	@Column(name="compliance_due_date")
	private Date complianceDueDate;
	
	@Column(name="closure_date")
	private Date closureDate;
	
	@Column(name="desk_compliance_score")
	private String deskComplianceScore;	
	
	@Column(name="desk_compliance_rating")
	private String deskComplianceRating;
	
	@Column(name="onsite_compliance_score")
	private String onsiteComplianceScore;
	
	@Column(name="onsite_compliance_rating")
	private String onsiteComplianceRating;
	
	@Column(name="rating_maker")
	private String ratingMaker;
	  
	@Column(name="rating_maker_timestamp")
	private Date ratingMakerTimestamp;
	
	@Column(name="rating_checker")
	private String ratingChecker;
	
	@Column(name="rating_checker_timestamp")
	private Date ratingCheckerTimestamp;
	
	@Column(name="rating_status")
	private String ratingStatus;
	
	@Column(name="rating_auth_rej_remarks")
	private String ratingAuthRejRemarks;
	
	@Column(name="financial_year")
	private Date financialYear;
	
	@Column(name="audit_serial_no")
	private Integer auditSerialNo;
	
	@Column(name="report_language")
	private String reportLanguage;
	
	@Column(name="rating_parameter_status")
	private String ratingParameterStatus;
	
	@Column(name="rating_parameter_maker")
	private String ratingParameterMaker;
	
	@Column(name="rating_parameter_reviewer_assigned")
	private String ratingParameterReviewerAssigned;
		
	@Column(name="rating_parameter_reviewer")
	private String ratingParameterReviewer;	
	
	@Column(name="risk_score")
	private String riskScore;	

	@Column(name="risk_rating")
	private String riskRating;	
	
	@Column(name="control_score")
	private String controlScore;	
	
	@Column(name="control_rating")
	private String controlRating;	
	
	@Column(name="risk_score_rcsa")
	private String riskScoreRCSA;	

	@Column(name="risk_rating_rcsa")
	private String riskRatingRCSA;	
	
	@Column(name="control_score_rcsa")
	private String controlScoreRCSA;	
	
	@Column(name="control_rating_rcsa")
	private String controlRatingRCSA;	
	
	@Column(name="residual_risk")
	private String residualRisk;
	
	@Column(name="residual_risk_rcsa")
	private String residualRiskRCSA;
	
	@Column(name="risk_statement")
	private String riskStatement;
	
	private transient String reportStatusDesc;
	private transient String allocatorName;
	private transient String reportReviewerName;
	
	private transient String parentUnitCodeName;
	
	// Used for Letter user input Authorization through Update Plan
	private transient String userInput01;
	private transient String userInput02;
	private transient String userInput03;
	private transient String userInput04;
	private transient String userInput05;
	private transient String userInput06;
	private transient String userInput07;
	private transient String userInput08;
	private transient String userInput09;
	private transient String userInput10; 	
	private transient String userInputMaker;
	private transient String userInputChecker;
	private transient Date userInputMakerTimeStamp;
	private transient String userInputAuthRejRemarks;
	private transient Date userInputBusinessdateTimestamp; 
	private transient Date userInputSysdateTimestamp;
	private transient String userInputStatus;
	
	private transient String rmCode;
	/*************** End Here, BaaSDOS *********************/
	
	 @OneToMany(fetch = FetchType.EAGER)
	    @JoinColumns({ 
	        @JoinColumn(name = "legal_entity_code", referencedColumnName = "legal_entity_code"), 
	        @JoinColumn(name = "audit_type_code", referencedColumnName = "audit_type_code"), 
	        @JoinColumn(name = "plan_id", referencedColumnName = "plan_id"),
	        @JoinColumn(name = "audit_id", referencedColumnName = "audit_id"),
	    })
	private List<AuditTeam> auditTeams;
	
	private transient String unitType;
	private transient String uCriticality;
//	private transient String duration;
//	private transient String frequency;
	// private transient String lastAuditDate;
	private transient String teamLead;
	private transient String teamMember;
//	private transient String manDays;
	private transient String unitName;
	
	private transient String criticalityDesc;
	private transient String member;
	private transient Date startDate;
	private transient Date endDate;
	private transient long rowNum;
	private transient String isUnitHeadUser;
	private transient Integer responseReject;
	private transient Integer complianceCount;
	private transient Integer nonComplianceCount;
	private transient Integer notApplicableCount;
	
	private transient String auditStatusDescription;
	private transient String earlyStartAllowed;
	
	private transient Integer auditOffsetDays;
	private transient Date auditOffsetDate;
	
	private transient String prevPrevAuditId;
	
	private transient String planValidationMismatchDesc;
	
	private transient Integer taskTotalCount;
	private transient Integer taskOpenCount;
	
	private transient String isForRatingWorkTmp;
	private transient Date targetEndDate;
	
	private transient String reviewStatusDesc;
	
	private transient String documentType;
	private transient String documentSubType;
	
	public AuditSchedule() {
	}

	
	
	/**
	 * @param legalEntityCode
	 * @param auditTypeCode
	 * @param planId
	 * @param unitId
	 * @param auditId
	 * @param scheduleStartDate
	 * @param scheduleEndDate
	 * @param actualStartDate
	 * @param actualEndDate
	 * @param auditStatus
	 * @param totalFindings
	 * @param openFindings
	 * @param rectifiedFindings
	 * @param closedFindings
	 * @param respondedFindings
	 * @param ratingId
	 * @param auditerId
	 * @param responderId
	 * @param reviewerId
	 * @param entityStatus
	 * @param status
	 * @param maker
	 * @param makerTimestamp
	 * @param checker
	 * @param checkerTimestamp
	 * @param businessdateTimestamp
	 * @param sysdateTimestamp
	 * @param auditTeams
	 */
	public AuditSchedule(AuditScheduleWrk auditScheduleWrk) {
		super();
		this.legalEntityCode = auditScheduleWrk.getLegalEntityCode();
		this.auditTypeCode = auditScheduleWrk.getAuditTypeCode();
		this.planId = auditScheduleWrk.getPlanId();
		this.planType= auditScheduleWrk.getPlanType();
		this.unitId = auditScheduleWrk.getUnitId();
		this.auditId = auditScheduleWrk.getAuditId();
		this.unitHead = auditScheduleWrk.getUnitHead();
		this.scheduleStartDate = auditScheduleWrk.getScheduleStartDate();
		this.scheduleEndDate = auditScheduleWrk.getScheduleEndDate();
		this.actualStartDate = auditScheduleWrk.getActualStartDate();
		this.actualEndDate = auditScheduleWrk.getActualEndDate();
		this.auditStatus = auditScheduleWrk.getAuditStatus();
		this.totalFindings = auditScheduleWrk.getTotalFindings();
		this.openFindings = auditScheduleWrk.getOpenFindings();
		this.rectifiedFindings = auditScheduleWrk.getRectifiedFindings();
		this.closedFindings = auditScheduleWrk.getClosedFindings();
		this.respondedFindings = auditScheduleWrk.getRespondedFindings();
		this.ratingId = auditScheduleWrk.getRatingId();
		this.assessedRiskPrcntg = auditScheduleWrk.getAssessedRiskPrcntg();
		this.assessedRating = auditScheduleWrk.getAssessedRating();
		this.effectiveRiskPrcntg = auditScheduleWrk.getEffectiveRiskPrcntg();
		this.effectiveRating = auditScheduleWrk.getEffectiveRating();
		this.ratingOverrideRemarks = auditScheduleWrk.getRatingOverrideRemarks();
		this.auditerId = auditScheduleWrk.getAuditerId();
		this.responderId = auditScheduleWrk.getResponderId();
		this.reviewerId = auditScheduleWrk.getReviewerId();
		this.entityStatus = auditScheduleWrk.getEntityStatus();
		this.status = auditScheduleWrk.getStatus();
		this.maker = auditScheduleWrk.getMaker();
		this.makerTimestamp = auditScheduleWrk.getMakerTimestamp();
		this.checker = auditScheduleWrk.getChecker();
		this.checkerTimestamp = auditScheduleWrk.getCheckerTimestamp();
		this.lastAuditDate = auditScheduleWrk.getLastAuditDate();
		this.reportDate = auditScheduleWrk.getReportDate();
		this.responseTime = auditScheduleWrk.getResponseTime();
		this.revenueLeakage = auditScheduleWrk.getRevenueLeakage();
		this.prevAuditId = auditScheduleWrk.getPrevAuditId();
		this.prevAssessedRiskPrcntg = auditScheduleWrk.getPrevAssessedRiskPrcntg();
		this.ratingImprovement = auditScheduleWrk.getRatingImprovement();
		this.auditConsiderForPlanning = auditScheduleWrk.getAuditConsiderForPlanning();
		this.frequency=auditScheduleWrk.getFrequency();
		this.duration=auditScheduleWrk.getDuration();
		this.manDays=auditScheduleWrk.getManDays();
		this.plannedAuditContinueDrop = auditScheduleWrk.getPlannedAuditContinueDrop();
		this.planType = auditScheduleWrk.getPlanType();
		this.planComments = auditScheduleWrk.getPlanComments();
		this.planAuditRemarks = auditScheduleWrk.getPlanAuditRemarks();
		this.planValidationMismatch = auditScheduleWrk.getPlanValidationMismatch();
		this.auditTeams = new ArrayList<AuditTeam>();
		if(auditScheduleWrk.getAuditTeamWrks()!=null){
			this.auditTeams=new ArrayList<>();
			for (AuditTeamWrk auditTeamWrk : auditScheduleWrk.getAuditTeamWrks()) {
				this.auditTeams.add(new AuditTeam(auditTeamWrk));
			}
		}	
	}



	/**
	 * @return the auditerId
	 */
	public String getAuditerId() {
		return auditerId;
	}

	/**
	 * @param auditerId the auditerId to set
	 */
	public void setAuditerId(String auditerId) {
		this.auditerId = auditerId;
	}

	/**
	 * @return the responderId
	 */
	public String getResponderId() {
		return responderId;
	}

	/**
	 * @param responderId the responderId to set
	 */
	public void setResponderId(String responderId) {
		this.responderId = responderId;
	}

	/**
	 * @return the reviewerId
	 */
	public String getReviewerId() {
		return reviewerId;
	}

	/**
	 * @param reviewerId the reviewerId to set
	 */
	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
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

	public String getUnitHead() {
		return unitHead;
	}



	public void setUnitHead(String unitHead) {
		this.unitHead = unitHead;
	}



	/**
	 * @return the scheduleStartDate
	 */
	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	/**
	 * @param scheduleStartDate the scheduleStartDate to set
	 */
	public void setScheduleStartDate(Date scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	/**
	 * @return the scheduleEndDate
	 */
	public Date getScheduleEndDate() {
		return scheduleEndDate;
	}

	/**
	 * @param scheduleEndDate the scheduleEndDate to set
	 */
	public void setScheduleEndDate(Date scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	/**
	 * @return the actualStartDate
	 */
	public Date getActualStartDate() {
		return actualStartDate;
	}

	/**
	 * @param actualStartDate the actualStartDate to set
	 */
	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	/**
	 * @return the actualEndDate
	 */
	public Date getActualEndDate() {
		return actualEndDate;
	}

	/**
	 * @param actualEndDate the actualEndDate to set
	 */
	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public Date getReportDate() {
		return reportDate;
	}



	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}



	/**
	 * @return the auditStatus
	 */
	public String getAuditStatus() {
		return auditStatus;
	}

	/**
	 * @param auditStatus the auditStatus to set
	 */
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getObservationStatus() {
		return observationStatus;
	}



	public void setObservationStatus(String observationStatus) {
		this.observationStatus = observationStatus;
	}



	public String getRespondStatus() {
		return respondStatus;
	}



	public void setRespondStatus(String respondStatus) {
		this.respondStatus = respondStatus;
	}



	public String getReviewStatus() {
		return reviewStatus;
	}



	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getRespondClosureStatus() {
		return respondClosureStatus;
	}
	public void setRespondClosureStatus(String respondClosureStatus) {
		this.respondClosureStatus = respondClosureStatus;
	}



	/**
	 * @return the totalFindings
	 */
	public Integer getTotalFindings() {
		return totalFindings;
	}

	/**
	 * @param totalFindings the totalFindings to set
	 */
	public void setTotalFindings(Integer totalFindings) {
		this.totalFindings = totalFindings;
	}

	/**
	 * @return the openFindings
	 */
	public Integer getOpenFindings() {
		return openFindings;
	}

	/**
	 * @param openFindings the openFindings to set
	 */
	public void setOpenFindings(Integer openFindings) {
		this.openFindings = openFindings;
	}

	/**
	 * @return the rectifiedFindings
	 */
	public Integer getRectifiedFindings() {
		return rectifiedFindings;
	}

	/**
	 * @param rectifiedFindings the rectifiedFindings to set
	 */
	public void setRectifiedFindings(Integer rectifiedFindings) {
		this.rectifiedFindings = rectifiedFindings;
	}

	/**
	 * @return the closedFindings
	 */
	public Integer getClosedFindings() {
		return closedFindings;
	}

	/**
	 * @param closedFindings the closedFindings to set
	 */
	public void setClosedFindings(Integer closedFindings) {
		this.closedFindings = closedFindings;
	}

	/**
	 * @return the respondedFindings
	 */
	public Integer getRespondedFindings() {
		return respondedFindings;
	}

	/**
	 * @param respondedFindings the respondedFindings to set
	 */
	public void setRespondedFindings(Integer respondedFindings) {
		this.respondedFindings = respondedFindings;
	}

	public Integer getResponseTime() {
		return responseTime;
	}



	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}



	public String getClosureTime() {
		return closureTime;
	}

	public void setClosureTime(String closureTime) {
		this.closureTime = closureTime;
	}

	public Integer getRevenueLeakage() {
		return revenueLeakage;
	}



	public void setRevenueLeakage(Integer revenueLeakage) {
		this.revenueLeakage = revenueLeakage;
	}



	/**
	 * @return the ratingId
	 */
	public String getRatingId() {
		return ratingId;
	}

	/**
	 * @param ratingId the ratingId to set
	 */
	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}

	

	public String getAssessedRiskPrcntg() {
		return assessedRiskPrcntg;
	}

	public void setAssessedRiskPrcntg(String assessedRiskPrcntg) {
		this.assessedRiskPrcntg = assessedRiskPrcntg;
	}

	public String getAssessedRating() {
		return assessedRating;
	}

	public void setAssessedRating(String assessedRating) {
		this.assessedRating = assessedRating;
	}

	public String getEffectiveRiskPrcntg() {
		return effectiveRiskPrcntg;
	}

	public void setEffectiveRiskPrcntg(String effectiveRiskPrcntg) {
		this.effectiveRiskPrcntg = effectiveRiskPrcntg;
	}

	public String getEffectiveRating() {
		return effectiveRating;
	}

	public void setEffectiveRating(String effectiveRating) {
		this.effectiveRating = effectiveRating;
	}

	public String getRatingOverrideRemarks() {
		return ratingOverrideRemarks;
	}

	public void setRatingOverrideRemarks(String ratingOverrideRemarks) {
		this.ratingOverrideRemarks = ratingOverrideRemarks;
	}

	/**
	 * @return the entityStatus
	 */
	public String getEntityStatus() {
		return entityStatus;
	}

	/**
	 * @param entityStatus the entityStatus to set
	 */
	public void setEntityStatus(String entityStatus) {
		this.entityStatus = entityStatus;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the maker
	 */
	public String getMaker() {
		return maker;
	}

	/**
	 * @param maker the maker to set
	 */
	public void setMaker(String maker) {
		this.maker = maker;
	}

	/**
	 * @return the makerTimestamp
	 */
	public Date getMakerTimestamp() {
		return makerTimestamp;
	}

	/**
	 * @param makerTimestamp the makerTimestamp to set
	 */
	public void setMakerTimestamp(Date makerTimestamp) {
		this.makerTimestamp = makerTimestamp;
	}

	/**
	 * @return the checker
	 */
	public String getChecker() {
		return checker;
	}

	/**
	 * @param checker the checker to set
	 */
	public void setChecker(String checker) {
		this.checker = checker;
	}

	/**
	 * @return the checkerTimestamp
	 */
	public Date getCheckerTimestamp() {
		return checkerTimestamp;
	}

	/**
	 * @param checkerTimestamp the checkerTimestamp to set
	 */
	public void setCheckerTimestamp(Date checkerTimestamp) {
		this.checkerTimestamp = checkerTimestamp;
	}

	public Date getRespondedDate() {
		return respondedDate;
	}



	public void setRespondedDate(Date respondedDate) {
		this.respondedDate = respondedDate;
	}


	public Date getRespondClosureDate() {
		return respondClosureDate;
	}

	public void setRespondClosureDate(Date respondClosureDate) {
		this.respondClosureDate = respondClosureDate;
	}



	public Date getReviewedDate() {
		return reviewedDate;
	}



	public void setReviewedDate(Date reviewedDate) {
		this.reviewedDate = reviewedDate;
	}



	public String getClosureUserId() {
		return closureUserId;
	}

	public void setClosureUserId(String closureUserId) {
		this.closureUserId = closureUserId;
	}

	/**
	 * @return the auditTeams
	 */
	public List<AuditTeam> getAuditTeams() {
		return auditTeams;
	}

	/**
	 * @param auditTeams the auditTeams to set
	 */
	public void setAuditTeams(List<AuditTeam> auditTeams) {
		this.auditTeams = auditTeams;
	}
	
	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}



	public String getuCriticality() {
		return uCriticality;
	}



	public void setuCriticality(String uCriticality) {
		this.uCriticality = uCriticality;
	}



	public String getTeamLead() {
		return teamLead;
	}



	public void setTeamLead(String teamLead) {
		this.teamLead = teamLead;
	}



	public String getDuration() {
		return duration;
	}



	public void setDuration(String duration) {
		this.duration = duration;
	}



	



	public Date getLastAuditDate() {
		return lastAuditDate;
	}



	public void setLastAuditDate(Date lastAuditDate) {
		this.lastAuditDate = lastAuditDate;
	}



	public String getTeamMember() {
		return teamMember;
	}



	public void setTeamMember(String teamMember) {
		this.teamMember = teamMember;
	}



	public String getFrequency() {
		return frequency;
	}



	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}



	public String getManDays() {
		return manDays;
	}



	public void setManDays(String manDays) {
		this.manDays = manDays;
	}



	public String getUnitName() {
		return unitName;
	}



	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}



	public String getCriticalityDesc() {
		return criticalityDesc;
	}



	public void setCriticalityDesc(String criticalityDesc) {
		this.criticalityDesc = criticalityDesc;
	}



	public String getMember() {
		return member;
	}



	public void setMember(String member) {
		this.member = member;
	}



	public Date getStartDate() {
		return startDate;
	}



	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}



	public Date getEndDate() {
		return endDate;
	}



	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getRowNum() {
		return rowNum;
	}

	public void setRowNum(long rowNum) {
		this.rowNum = rowNum;
	}
	
	public String getIsUnitHeadUser() {
		return isUnitHeadUser;
	}

	public void setIsUnitHeadUser(String isUnitHeadUser) {
		this.isUnitHeadUser = isUnitHeadUser;
	}

	public Integer getResponseReject() {
		return responseReject;
	}



	public void setResponseReject(Integer responseReject) {
		this.responseReject = responseReject;
	}



	public Integer getComplianceCount() {
		return complianceCount;
	}



	public void setComplianceCount(Integer complianceCount) {
		this.complianceCount = complianceCount;
	}



	public Integer getNonComplianceCount() {
		return nonComplianceCount;
	}

	public void setNonComplianceCount(Integer nonComplianceCount) {
		this.nonComplianceCount = nonComplianceCount;
	}

	public Integer getNotApplicableCount() {
		return notApplicableCount;
	}



	public void setNotApplicableCount(Integer notApplicableCount) {
		this.notApplicableCount = notApplicableCount;
	}



	public String getAuditStatusDescription() {
		return auditStatusDescription;
	}

	public void setAuditStatusDescription(String auditStatusDescription) {
		this.auditStatusDescription = auditStatusDescription;
	}



	public String getEarlyStartAllowed() {
		return earlyStartAllowed;
	}



	public void setEarlyStartAllowed(String earlyStartAllowed) {
		this.earlyStartAllowed = earlyStartAllowed;
	}



	public String getAuditConsiderForPlanning() {
		return auditConsiderForPlanning;
	}



	public void setAuditConsiderForPlanning(String auditConsiderForPlanning) {
		this.auditConsiderForPlanning = auditConsiderForPlanning;
	}
 


	public String getPrevAuditId() {
		return prevAuditId;
	}

	public void setPrevAuditId(String prevAuditId) {
		this.prevAuditId = prevAuditId;
	}

	public String getPrevAssessedRiskPrcntg() {
		return prevAssessedRiskPrcntg;
	}

	public void setPrevAssessedRiskPrcntg(String prevAssessedRiskPrcntg) {
		this.prevAssessedRiskPrcntg = prevAssessedRiskPrcntg;
	}

	public String getRatingImprovement() {
		return ratingImprovement;
	}

	public void setRatingImprovement(String ratingImprovement) {
		this.ratingImprovement = ratingImprovement;
	}

	public String getPlannedAuditContinueDrop() {
		return plannedAuditContinueDrop;
	}

	public void setPlannedAuditContinueDrop(String plannedAuditContinueDrop) {
		this.plannedAuditContinueDrop = plannedAuditContinueDrop;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}
	
	public String getPlanComments() {
		return planComments;
	}

	public void setPlanComments(String planComments) {
		this.planComments = planComments;
	}

	public String getPlanAuditRemarks() {
		return planAuditRemarks;
	}

	public void setPlanAuditRemarks(String planAuditRemarks) {
		this.planAuditRemarks = planAuditRemarks;
	}
	
	public String getPlanValidationMismatch() {
		return planValidationMismatch;
	}

	public void setPlanValidationMismatch(String planValidationMismatch) {
		this.planValidationMismatch = planValidationMismatch;
	}
	
	public String getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(String auditCategory) {
		this.auditCategory = auditCategory;
	}

	public String getTransactionAllowed() {
		return transactionAllowed;
	}

	public void setTransactionAllowed(String transactionAllowed) {
		this.transactionAllowed = transactionAllowed;
	}

	public String getIoFlexibility() {
		return ioFlexibility;
	}

	public void setIoFlexibility(String ioFlexibility) {
		this.ioFlexibility = ioFlexibility;
	}

	public Integer getAuditOffsetDays() {
		return auditOffsetDays;
	}

	public void setAuditOffsetDays(Integer auditOffsetDays) {
		this.auditOffsetDays = auditOffsetDays;
	}

	public Date getAuditOffsetDate() {
		return auditOffsetDate;
	}

	public void setAuditOffsetDate(Date auditOffsetDate) {
		this.auditOffsetDate = auditOffsetDate;
	}

	public String getPrevPrevAuditId() {
		return prevPrevAuditId;
	}

	public void setPrevPrevAuditId(String prevPrevAuditId) {
		this.prevPrevAuditId = prevPrevAuditId;
	}

	public String getPlanValidationMismatchDesc() {
		return planValidationMismatchDesc;
	}

	public void setPlanValidationMismatchDesc(String planValidationMismatchDesc) {
		this.planValidationMismatchDesc = planValidationMismatchDesc;
	}

	public String getInterfaceAccFetch() {
		return interfaceAccFetch;
	}

	public void setInterfaceAccFetch(String interfaceAccFetch) {
		this.interfaceAccFetch = interfaceAccFetch;
	}

	public String getInhrtRiskCode() {
		return inhrtRiskCode;
	}

	public void setInhrtRiskCode(String inhrtRiskCode) {
		this.inhrtRiskCode = inhrtRiskCode;
	}

	public String getInhrtRiskDesc() {
		return inhrtRiskDesc;
	}

	public void setInhrtRiskDesc(String inhrtRiskDesc) {
		this.inhrtRiskDesc = inhrtRiskDesc;
	}

	public String getInhrtRiskScore() {
		return inhrtRiskScore;
	}

	public void setInhrtRiskScore(String inhrtRiskScore) {
		this.inhrtRiskScore = inhrtRiskScore;
	}

	public String getCntrlClsBucketCode() {
		return cntrlClsBucketCode;
	}

	public void setCntrlClsBucketCode(String cntrlClsBucketCode) {
		this.cntrlClsBucketCode = cntrlClsBucketCode;
	}

	public String getCntrlClsBucketDesc() {
		return cntrlClsBucketDesc;
	}

	public void setCntrlClsBucketDesc(String cntrlClsBucketDesc) {
		this.cntrlClsBucketDesc = cntrlClsBucketDesc;
	}

	public String getCntrlClsBucketScore() {
		return cntrlClsBucketScore;
	}

	public void setCntrlClsBucketScore(String cntrlClsBucketScore) {
		this.cntrlClsBucketScore = cntrlClsBucketScore;
	}

	public String getResidulRiskCode() {
		return residulRiskCode;
	}

	public void setResidulRiskCode(String residulRiskCode) {
		this.residulRiskCode = residulRiskCode;
	}

	public String getResidulRiskDesc() {
		return residulRiskDesc;
	}

	public void setResidulRiskDesc(String residulRiskDesc) {
		this.residulRiskDesc = residulRiskDesc;
	}

	public String getResidulRiskScore() {
		return residulRiskScore;
	}

	public void setResidulRiskScore(String residulRiskScore) {
		this.residulRiskScore = residulRiskScore;
	}

	public String getRiskWeightagePercentage() {
		return riskWeightagePercentage;
	}

	public void setRiskWeightagePercentage(String riskWeightagePercentage) {
		this.riskWeightagePercentage = riskWeightagePercentage;
	}

	public String getTotalNoRisk() {
		return totalNoRisk;
	}

	public void setTotalNoRisk(String totalNoRisk) {
		this.totalNoRisk = totalNoRisk;
	}

	public String getTotalWeightage() {
		return totalWeightage;
	}

	public void setTotalWeightage(String totalWeightage) {
		this.totalWeightage = totalWeightage;
	}

	public String getRiskIndexScore() {
		return riskIndexScore;
	}

	public void setRiskIndexScore(String riskIndexScore) {
		this.riskIndexScore = riskIndexScore;
	}

	public String getControlProfileCode() {
		return controlProfileCode;
	}

	public void setControlProfileCode(String controlProfileCode) {
		this.controlProfileCode = controlProfileCode;
	}

	public String getControlProfileDesc() {
		return controlProfileDesc;
	}

	public void setControlProfileDesc(String controlProfileDesc) {
		this.controlProfileDesc = controlProfileDesc;
	}

	public String getControlProfileScore() {
		return controlProfileScore;
	}

	public void setControlProfileScore(String controlProfileScore) {
		this.controlProfileScore = controlProfileScore;
	}

	public String getHealthIndexCode() {
		return healthIndexCode;
	}

	public void setHealthIndexCode(String healthIndexCode) {
		this.healthIndexCode = healthIndexCode;
	}

	public String getHealthIndexDsec() {
		return healthIndexDsec;
	}

	public void setHealthIndexDsec(String healthIndexDsec) {
		this.healthIndexDsec = healthIndexDsec;
	}

	public String getHealthIndexScore() {
		return healthIndexScore;
	}

	public void setHealthIndexScore(String healthIndexScore) {
		this.healthIndexScore = healthIndexScore;
	}

	public String getTeamMaker() {
		return teamMaker;
	}

	public void setTeamMaker(String teamMaker) {
		this.teamMaker = teamMaker;
	}

	public Date getTeamMakerTimestamp() {
		return teamMakerTimestamp;
	}

	public void setTeamMakerTimestamp(Date teamMakerTimestamp) {
		this.teamMakerTimestamp = teamMakerTimestamp;
	}

	public String getTeamChecker() {
		return teamChecker;
	}

	public void setTeamChecker(String teamChecker) {
		this.teamChecker = teamChecker;
	}

	public Date getTeamCheckerTimestamp() {
		return teamCheckerTimestamp;
	}

	public void setTeamCheckerTimestamp(Date teamCheckerTimestamp) {
		this.teamCheckerTimestamp = teamCheckerTimestamp;
	}

	public String getTeamStatus() {
		return teamStatus;
	}

	public void setTeamStatus(String teamStatus) {
		this.teamStatus = teamStatus;
	}

	public String getTeamAuthRejRemarks() {
		return teamAuthRejRemarks;
	}

	public void setTeamAuthRejRemarks(String teamAuthRejRemarks) {
		this.teamAuthRejRemarks = teamAuthRejRemarks;
	}

	public Integer getTaskTotalCount() {
		return taskTotalCount;
	}

	public void setTaskTotalCount(Integer taskTotalCount) {
		this.taskTotalCount = taskTotalCount;
	}

	public Integer getTaskOpenCount() {
		return taskOpenCount;
	}

	public void setTaskOpenCount(Integer taskOpenCount) {
		this.taskOpenCount = taskOpenCount;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getAllocator() {
		return allocator;
	}

	public void setAllocator(String allocator) {
		this.allocator = allocator;
	}

	public String getReportReviewer() {
		return reportReviewer;
	}

	public void setReportReviewer(String reportReviewer) {
		this.reportReviewer = reportReviewer;
	}

	public Date getReportReviewerStartDate() {
		return reportReviewerStartDate;
	}

	public void setReportReviewerStartDate(Date reportReviewerStartDate) {
		this.reportReviewerStartDate = reportReviewerStartDate;
	}

	public Date getReportReviewerTargetDate() {
		return reportReviewerTargetDate;
	}

	public void setReportReviewerTargetDate(Date reportReviewerTargetDate) {
		this.reportReviewerTargetDate = reportReviewerTargetDate;
	}

	public Date getReportTargetDate() {
		return reportTargetDate;
	}

	public void setReportTargetDate(Date reportTargetDate) {
		this.reportTargetDate = reportTargetDate;
	}

	public Date getReportReviewEndDate() {
		return reportReviewEndDate;
	}

	public void setReportReviewEndDate(Date reportReviewEndDate) {
		this.reportReviewEndDate = reportReviewEndDate;
	}

	public Date getReportDispatchDate() {
		return reportDispatchDate;
	}

	public void setReportDispatchDate(Date reportDispatchDate) {
		this.reportDispatchDate = reportDispatchDate;
	}

	public Date getIrSubmissionDate() {
		return irSubmissionDate;
	}

	public void setIrSubmissionDate(Date irSubmissionDate) {
		this.irSubmissionDate = irSubmissionDate;
	}

	public Date getIrFinalisationDate() {
		return irFinalisationDate;
	}

	public void setIrFinalisationDate(Date irFinalisationDate) {
		this.irFinalisationDate = irFinalisationDate;
	}

	public Date getComplianceDueDate() {
		return complianceDueDate;
	}

	public void setComplianceDueDate(Date complianceDueDate) {
		this.complianceDueDate = complianceDueDate;
	}

	public Date getClosureDate() {
		return closureDate;
	}

	public void setClosureDate(Date closureDate) {
		this.closureDate = closureDate;
	}

	public String getReportStatusDesc() {
		return reportStatusDesc;
	}

	public void setReportStatusDesc(String reportStatusDesc) {
		this.reportStatusDesc = reportStatusDesc;
	}

	public String getAllocatorName() {
		return allocatorName;
	}

	public void setAllocatorName(String allocatorName) {
		this.allocatorName = allocatorName;
	}

	public String getReportReviewerName() {
		return reportReviewerName;
	}

	public void setReportReviewerName(String reportReviewerName) {
		this.reportReviewerName = reportReviewerName;
	}

	public String getDeskComplianceScore() {
		return deskComplianceScore;
	}

	public void setDeskComplianceScore(String deskComplianceScore) {
		this.deskComplianceScore = deskComplianceScore;
	}

	public String getDeskComplianceRating() {
		return deskComplianceRating;
	}

	public void setDeskComplianceRating(String deskComplianceRating) {
		this.deskComplianceRating = deskComplianceRating;
	}

	public String getOnsiteComplianceScore() {
		return onsiteComplianceScore;
	}

	public void setOnsiteComplianceScore(String onsiteComplianceScore) {
		this.onsiteComplianceScore = onsiteComplianceScore;
	}

	public String getOnsiteComplianceRating() {
		return onsiteComplianceRating;
	}

	public void setOnsiteComplianceRating(String onsiteComplianceRating) {
		this.onsiteComplianceRating = onsiteComplianceRating;
	}

	public String getRatingMaker() {
		return ratingMaker;
	}

	public void setRatingMaker(String ratingMaker) {
		this.ratingMaker = ratingMaker;
	}

	public Date getRatingMakerTimestamp() {
		return ratingMakerTimestamp;
	}

	public void setRatingMakerTimestamp(Date ratingMakerTimestamp) {
		this.ratingMakerTimestamp = ratingMakerTimestamp;
	}

	public String getRatingChecker() {
		return ratingChecker;
	}

	public void setRatingChecker(String ratingChecker) {
		this.ratingChecker = ratingChecker;
	}

	public Date getRatingCheckerTimestamp() {
		return ratingCheckerTimestamp;
	}

	public void setRatingCheckerTimestamp(Date ratingCheckerTimestamp) {
		this.ratingCheckerTimestamp = ratingCheckerTimestamp;
	}

	public String getRatingStatus() {
		return ratingStatus;
	}

	public void setRatingStatus(String ratingStatus) {
		this.ratingStatus = ratingStatus;
	}

	public String getRatingAuthRejRemarks() {
		return ratingAuthRejRemarks;
	}

	public void setRatingAuthRejRemarks(String ratingAuthRejRemarks) {
		this.ratingAuthRejRemarks = ratingAuthRejRemarks;
	}

	public String getIsForRatingWorkTmp() {
		return isForRatingWorkTmp;
	}

	public void setIsForRatingWorkTmp(String isForRatingWorkTmp) {
		this.isForRatingWorkTmp = isForRatingWorkTmp;
	}

	public Date getTargetEndDate() {
		return targetEndDate;
	}

	public void setTargetEndDate(Date targetEndDate) {
		this.targetEndDate = targetEndDate;
	}

	public String getReviewStatusDesc() {
		return reviewStatusDesc;
	}

	public void setReviewStatusDesc(String reviewStatusDesc) {
		this.reviewStatusDesc = reviewStatusDesc;
	}

	public Date getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(Date financialYear) {
		this.financialYear = financialYear;
	}

	public Integer getAuditSerialNo() {
		return auditSerialNo;
	}

	public void setAuditSerialNo(Integer auditSerialNo) {
		this.auditSerialNo = auditSerialNo;
	}

	public String getReportLanguage() {
		return reportLanguage;
	}

	public void setReportLanguage(String reportLanguage) {
		this.reportLanguage = reportLanguage;
	}

	public String getRatingParameterStatus() {
		return ratingParameterStatus;
	}

	public void setRatingParameterStatus(String ratingParameterStatus) {
		this.ratingParameterStatus = ratingParameterStatus;
	}

	public String getRatingParameterMaker() {
		return ratingParameterMaker;
	}

	public void setRatingParameterMaker(String ratingParameterMaker) {
		this.ratingParameterMaker = ratingParameterMaker;
	}

	public String getRatingParameterReviewerAssigned() {
		return ratingParameterReviewerAssigned;
	}

	public void setRatingParameterReviewerAssigned(String ratingParameterReviewerAssigned) {
		this.ratingParameterReviewerAssigned = ratingParameterReviewerAssigned;
	}

	public String getRatingParameterReviewer() {
		return ratingParameterReviewer;
	}

	public void setRatingParameterReviewer(String ratingParameterReviewer) {
		this.ratingParameterReviewer = ratingParameterReviewer;
	}

	public String getParentUnitCodeName() {
		return parentUnitCodeName;
	}

	public void setParentUnitCodeName(String parentUnitCodeName) {
		this.parentUnitCodeName = parentUnitCodeName;
	}

	public String getUserInput01() {
		return userInput01;
	}

	public void setUserInput01(String userInput01) {
		this.userInput01 = userInput01;
	}

	public String getUserInput02() {
		return userInput02;
	}

	public void setUserInput02(String userInput02) {
		this.userInput02 = userInput02;
	}

	public String getUserInput03() {
		return userInput03;
	}

	public void setUserInput03(String userInput03) {
		this.userInput03 = userInput03;
	}

	public String getUserInput04() {
		return userInput04;
	}

	public void setUserInput04(String userInput04) {
		this.userInput04 = userInput04;
	}

	public String getUserInput05() {
		return userInput05;
	}

	public void setUserInput05(String userInput05) {
		this.userInput05 = userInput05;
	}

	public String getUserInput06() {
		return userInput06;
	}

	public void setUserInput06(String userInput06) {
		this.userInput06 = userInput06;
	}

	public String getUserInput07() {
		return userInput07;
	}

	public void setUserInput07(String userInput07) {
		this.userInput07 = userInput07;
	}

	public String getUserInput08() {
		return userInput08;
	}

	public void setUserInput08(String userInput08) {
		this.userInput08 = userInput08;
	}

	public String getUserInput09() {
		return userInput09;
	}

	public void setUserInput09(String userInput09) {
		this.userInput09 = userInput09;
	}

	public String getUserInput10() {
		return userInput10;
	}

	public void setUserInput10(String userInput10) {
		this.userInput10 = userInput10;
	}

	public String getUserInputMaker() {
		return userInputMaker;
	}

	public void setUserInputMaker(String userInputMaker) {
		this.userInputMaker = userInputMaker;
	}

	public String getUserInputChecker() {
		return userInputChecker;
	}

	public void setUserInputChecker(String userInputChecker) {
		this.userInputChecker = userInputChecker;
	}

	public Date getUserInputMakerTimeStamp() {
		return userInputMakerTimeStamp;
	}

	public void setUserInputMakerTimeStamp(Date userInputMakerTimeStamp) {
		this.userInputMakerTimeStamp = userInputMakerTimeStamp;
	}

	public String getUserInputAuthRejRemarks() {
		return userInputAuthRejRemarks;
	}

	public void setUserInputAuthRejRemarks(String userInputAuthRejRemarks) {
		this.userInputAuthRejRemarks = userInputAuthRejRemarks;
	}

	public Date getUserInputBusinessdateTimestamp() {
		return userInputBusinessdateTimestamp;
	}

	public void setUserInputBusinessdateTimestamp(Date userInputBusinessdateTimestamp) {
		this.userInputBusinessdateTimestamp = userInputBusinessdateTimestamp;
	}

	public Date getUserInputSysdateTimestamp() {
		return userInputSysdateTimestamp;
	}

	public void setUserInputSysdateTimestamp(Date userInputSysdateTimestamp) {
		this.userInputSysdateTimestamp = userInputSysdateTimestamp;
	}

	public String getUserInputStatus() {
		return userInputStatus;
	}

	public void setUserInputStatus(String userInputStatus) {
		this.userInputStatus = userInputStatus;
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

	public Date getRespondInitiatedDate() {
		return respondInitiatedDate;
	}

	public void setRespondInitiatedDate(Date respondInitiatedDate) {
		this.respondInitiatedDate = respondInitiatedDate;
	}

	public Date getReviewInitiatedDate() {
		return reviewInitiatedDate;
	}

	public void setReviewInitiatedDate(Date reviewInitiatedDate) {
		this.reviewInitiatedDate = reviewInitiatedDate;
	}

	public String getRmCode() {
		return rmCode;
	}

	public void setRmCode(String rmCode) {
		this.rmCode = rmCode;
	}



	public String getRiskScore() {
		return riskScore;
	}



	public void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}



	public String getRiskRating() {
		return riskRating;
	}



	public void setRiskRating(String riskRating) {
		this.riskRating = riskRating;
	}



	public String getControlScore() {
		return controlScore;
	}



	public void setControlScore(String controlScore) {
		this.controlScore = controlScore;
	}



	public String getControlRating() {
		return controlRating;
	}



	public void setControlRating(String controlRating) {
		this.controlRating = controlRating;
	}



	public String getRiskScoreRCSA() {
		return riskScoreRCSA;
	}



	public void setRiskScoreRCSA(String riskScoreRCSA) {
		this.riskScoreRCSA = riskScoreRCSA;
	}



	public String getRiskRatingRCSA() {
		return riskRatingRCSA;
	}



	public void setRiskRatingRCSA(String riskRatingRCSA) {
		this.riskRatingRCSA = riskRatingRCSA;
	}



	public String getControlScoreRCSA() {
		return controlScoreRCSA;
	}



	public void setControlScoreRCSA(String controlScoreRCSA) {
		this.controlScoreRCSA = controlScoreRCSA;
	}



	public String getControlRatingRCSA() {
		return controlRatingRCSA;
	}



	public void setControlRatingRCSA(String controlRatingRCSA) {
		this.controlRatingRCSA = controlRatingRCSA;
	}



	public String getResidualRiskRCSA() {
		return residualRiskRCSA;
	}



	public void setResidualRiskRCSA(String residualRiskRCSA) {
		this.residualRiskRCSA = residualRiskRCSA;
	}



	public String getResidualRisk() {
		return residualRisk;
	}



	public void setResidualRisk(String residualRisk) {
		this.residualRisk = residualRisk;
	}



	public String getRiskStatement() {
		return riskStatement;
	}



	public void setRiskStatement(String riskStatement) {
		this.riskStatement = riskStatement;
	}
			
}
