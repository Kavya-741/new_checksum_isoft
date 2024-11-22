package com.bankaudit.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "maint_entity_wrk")
public class MaintEntityWrk implements Serializable {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "legal_entity_code", insertable = false, updatable = false)
	private MaintLegalEntity maintLegalEntity;

	// @ManyToOne(fetch = FetchType.EAGER)
	// @JoinColumns({
	// 		@JoinColumn(name = "legal_entity_code", referencedColumnName = "legal_entity_code", insertable = false, updatable = false),
	// 		@JoinColumn(name = "level_code", referencedColumnName = "level_code", insertable = false, updatable = false)
	// })
	// private EntityLevelCodeDesc entityLevelCodeDesc;

	@Column(name = "level_code")
	private String levelCode;

	@Column(name = "parent_unit_code")
	private String parentUnitCode;

	@Id
	@Column(name = "unit_code")
	private String unitCode;

	@Column(name = "unit_name")
	private String unitName;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "address3")
	private String address3;

	@Column(name = "city")
	private String city;

	@Column(name = "pincode")
	private Integer pincode;

	@Column(name = "district")
	private String district;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "contact_person")
	private String contactPerson;

	@Column(name = "landline1")
	private String landline1;

	@Column(name = "landline2")
	private String landline2;

	@Column(name = "mobile1")
	private String mobile1;

	@Column(name = "mobile2")
	private String mobile2;

	@Column(name = "mailid")
	private String mailid;

	@Column(name = "u_criticality")
	private String uCriticality;

	@Column(name = "unit_type")
	private String unitType;

	@Column(name = "classification")
	private String classification;

	@Column(name = "type_of_entity")
	private String typeOfEntity;

	@Column(name = "weekly_holiday")
	private String weeklyHoliday;

	@Column(name = "fortnightly_holiday")
	private String fortnightlyHoliday;

	@Column(name = "unit_open_date")
	private Date unitOpenDate;

	@Column(name = "unit_head_user")
	private String unitHeadUser;

	@Column(name = "unit_head_inception_date")
	private Date unitHeadInceptionDate;

	@Column(name = "auth_rej_remarks")
	private String authRejRemarks;

	@Column(name = "status")
	private String status;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

	private transient List<MaintEntityAuditSubgroupMappingWrk> maintEntityAuditSubgroupMappings;

}
