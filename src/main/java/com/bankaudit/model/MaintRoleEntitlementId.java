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
public class MaintRoleEntitlementId implements Serializable{

    private Integer legalEntityCode;

    private String ugRoleCode;

    private String functionId;
}
