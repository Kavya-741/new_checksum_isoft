package com.bankaudit.model;

import java.io.Serializable;
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