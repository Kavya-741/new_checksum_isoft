package com.bankaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="entity_level_code_desc")
public class EntityLevelCodeDesc implements Serializable {

	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id        
	@Column(name="level_code")
	private String levelCode;
	
	@Column(name="level_desc")
	private String levelDesc;
	
	@Column(name="auth_rej_remarks")
	private String authRejRemarks;
	
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
	
}
