package com.finakon.baas.entities.IdClasses;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class MaintAuditTypeDescId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer legalEntityCode;
	private String auditTypeCode;
}
