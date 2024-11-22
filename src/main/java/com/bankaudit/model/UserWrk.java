package com.bankaudit.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_wrk")
@IdClass(UserId.class)
public class UserWrk implements Serializable {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Id
	@Column(name = "user_id")
	private String userId;

	@Column(name = "unit_code")
	private String unitCode;

	@Column(name = "email_id")
	private String emailId;

	@Column(name = "password")
	private String password;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "current_login")
	private Date currentLogin;

	@Column(name = "last_login")
	private Date lastLogin;

	@Column(name = "unsuccessful_attempts")
	private Integer unsuccessfulAttempts;

	@Column(name = "type_of_user")
	private String typeOfUser;

	@Column(name = "gender")
	private String gender;

	@Column(name = "salutation")
	private String salutation;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "short_name")
	private String shortName;

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

	@Column(name = "landline1")
	private String landline1;

	@Column(name = "landline2")
	private String landline2;

	@Column(name = "mobile1")
	private String mobile1;

	@Column(name = "mobile2")
	private String mobile2;

	@Column(name = "auth_rej_remarks")
	private String authRejRemarks;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "status")
	private String status;

	@Column(name = "maker")
	private String maker;

	@Column(name = "maker_timestamp")
	private Date makerTimestamp;

	@Column(name = "checker")
	private String checker;

	@Column(name = "checker_timestamp")
	private Date checkerTimestamp;

	@Column(name = "grade")
	private String grade;

	@Column(name = "designation")
	private String designation;

	@Column(name = "department")
	private String department;

	// private transient List<MaintEntityAuditSubgroupMappingWrk>
	// maintEntityAuditSubgroupMappings;

	private transient String userRoles;

}
