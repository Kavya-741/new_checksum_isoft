package com.bankaudit.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class MaintCriticalityId implements Serializable{

	private Integer legalEntityCode;

	private String criticalityCode;
	

	private String criticalityOfType;
}
