package com.bankaudit.model;

import java.io.Serializable;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

@Entity
@Table(name="maint_audit_group")
@Data
public class MaintAuditGroup implements Serializable {
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="audit_type_code")
	private String auditTypeCode;
	
	@Id
	@Column(name="audit_group_code")
	private String auditGroupCode;
	

	@Column(name="audit_group_name")
	private String auditGroupName;
	
	@Column(name="audit_group_name_hindi")
	private String auditGroupNameHindi;
	
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
	
	
	 @OneToMany(fetch=FetchType.LAZY)
	    @JoinColumns({ 
	        @JoinColumn(name = "legal_entity_code", referencedColumnName = "legal_entity_code",insertable=false,updatable=false), 
	        @JoinColumn(name = "audit_type_code", referencedColumnName = "audit_type_code",insertable=false,updatable=false), 
	        @JoinColumn(name = "audit_group_code", referencedColumnName = "audit_group_code",insertable=false,updatable=false)
	    })
	private List<MaintAuditSubgroup> maintAuditSubgroups;

}
