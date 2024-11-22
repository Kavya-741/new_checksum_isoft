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
@Table(name="user_dept_mapping_hst")
public class UserDeptMappingHst implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="legal_entity_code")
	private Integer legalEntityCode;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="department_code")
	private String departmentCode;
	
	@Column(name="entity_status")
	private String entityStatus;
	
	@Column(name="status")
	private String status;
}
