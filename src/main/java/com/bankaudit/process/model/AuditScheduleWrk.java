package com.bankaudit.process.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;

@Entity
@Table(name="audit_schedule_wrk")
@Data
public class AuditScheduleWrk implements Serializable{
	
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
	
	@Column(name="prev_audit_id")
	private String prevAuditId; 
	
	@Column(name="prev_assessed_risk_prcntg")
	private String prevAssessedRiskPrcntg;
		
	@Column(name="rating_improvement")
	private String ratingImprovement; 
	
	@Column(name="audit_consider_for_planning")
	private String auditConsiderForPlanning;
	
	@Column(name="planned_audit_continue_drop")
	private String plannedAuditContinueDrop;
	
	@Column(name="plan_type")
	private String planType;

	@Column(name="plan_comments")
	private String planComments; 
	
	@Column(name="plan_audit_remarks")
	private String planAuditRemarks;
	
	@Column(name="frequency")
	private String frequency; 
	
	@Column(name="duration")
	private String duration; 
	
	@Column(name="man_days")
	private String manDays; 
	
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
	/*************** End Here, BaaSDOS *********************/
	
	@LazyCollection(LazyCollectionOption.FALSE)
	 @OneToMany()
	    @JoinColumns({ 
	        @JoinColumn(name = "legal_entity_code", referencedColumnName = "legal_entity_code"), 
	        @JoinColumn(name = "audit_type_code", referencedColumnName = "audit_type_code"), 
	        @JoinColumn(name = "plan_id", referencedColumnName = "plan_id"),
	        @JoinColumn(name = "audit_id", referencedColumnName = "audit_id"),
	    })
	private List<AuditTeamWrk> auditTeamWrks;
	 
	private transient String unitType;
	private transient String uCriticality;
	private transient String teamLead;
	private transient String teamMember;
	private transient String unitName;
	private transient String criticalityDesc;
	private transient String member;
	private transient Date startDate;
	private transient Date endDate;
	private transient long rowNum; 
	private transient String auditStatusDescription;
	private transient String planValidationMismatchDesc;
}
