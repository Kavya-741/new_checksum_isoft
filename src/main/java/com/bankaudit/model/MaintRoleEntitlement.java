package com.bankaudit.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="maint_role_entitlement")
@IdClass(MaintRoleEntitlementId.class)
public class MaintRoleEntitlement implements Serializable {


	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="ug_role_code")
	private String ugRoleCode;
	
	@Id
	@Column(name="function_id")
	private String functionId;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="maker",updatable=false)
	private String maker;
	
	@Column(name="maker_timestamp",updatable=false)
	private Date makerTimestamp;
	
	@Column(name="checker")
	private String checker;
	
	@Column(name="checker_timestamp")
	private Date checkerTimestamp;

	@Column(name="l1fname")
	private String l1Fname;

	@Column(name="l2fname")
	private String l2Fname;

	@Column(name="l3fname")
	private String l3Fname;

	@Column(name="l4fname")
	private String l4Fname;

	
	@Transient
	ArrayList<String> functionIds;

	public ArrayList<String> getFunctionIds() {
		return functionIds;
	}

	public void setFunctionIds(ArrayList<String> functionIds) {
		this.functionIds = functionIds;
	}

	


	
	
	
}
