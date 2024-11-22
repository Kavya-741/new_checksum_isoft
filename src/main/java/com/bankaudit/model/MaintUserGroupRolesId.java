package com.bankaudit.model;

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
public class MaintUserGroupRolesId implements Serializable {

    private Integer legalEntityCode;

    private String ugRoleCode;

}
