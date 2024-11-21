package com.finakon.baas.entities.IdClasses;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserRoleMappingId implements Serializable {
	private Integer legalEntityCode;
	private String userId;
	private String userRoleId;

}