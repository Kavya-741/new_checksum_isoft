package com.finakon.baas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GeneralParameterIdClass.class)
@Table(name="general_parameter")
public class GeneralParameter {
	
	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="mod_code")
	private String modCode;
	
	@Id
	@Column(name="language")
	private String language;
	
	@Id
	@Column(name="key_1")
	private String key1;
	
	@Id
	@Column(name="key_2")
	private String key2;
	
	@Id
	@Column(name="value")
	private String value;
	

	@Column(name="description")
	private String description;
	
	@Column(name="maker")
	private String maker;
	
	@Column(name="maker_timestamp")
	private Date makerTimestamp;
	
	@Column(name="checker")
	private String checker;
	
	@Column(name="checker_timestamp")
	private Date checkerTimestamp;
	
	@Column(name="businessdate_timestamp")
	private Date businessdateTimestamp;
	
	@Column(name="sysdate_timestamp")
	private Date sysdateTimestamp;

}
