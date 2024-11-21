package com.finakon.baas.entities.IdClasses;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserDeptMappingId implements Serializable{
	private Integer legalEntityCode;
	private String userId;
	private String departmentCode;
}