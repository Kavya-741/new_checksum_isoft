package com.finakon.baas.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.finakon.baas.entities.IdClasses.UserDeptMappingId;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_dept_mapping")
@IdClass(UserDeptMappingId.class)
public class UserDeptMapping {

	@Id
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Id
	@Column(name="user_id")
	private String userId;
	
	@Id
	@Column(name="department_code")
	private String departmentCode;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="status")
	private String status;

}
