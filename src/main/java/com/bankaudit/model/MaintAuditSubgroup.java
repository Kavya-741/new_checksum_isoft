package com.bankaudit.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="maint_audit_subgroup")
@IdClass(MaintAuditSubgroupId.class)
public class MaintAuditSubgroup implements Serializable {

	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="audit_group_code")
	private String auditGroupCode;
	
	@Id
	@Column(name="audit_sub_group_code")
	private String auditSubGroupCode;

	@Column(name="audit_sub_group_name")
	private String auditSubGroupName;
	
	@Column(name="audit_sub_group_name_hindi")
	private String auditSubGroupNameHindi;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;
	
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

}
