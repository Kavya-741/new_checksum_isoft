package com.bankaudit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_dept_mapping_wrk")
@IdClass(UserDeptMappingId.class)
public class UserDeptMappingWrk implements Serializable {

	@Id
	@Column(name = "legal_entity_code")
	private Integer legalEntityCode;

	@Id
	@Column(name = "user_id")
	private String userId;

	@Id
	@Column(name = "department_code")
	private String departmentCode;

	@Column(name = "entity_status")
	private String entityStatus;

	@Column(name = "status")
	private String status;

}
