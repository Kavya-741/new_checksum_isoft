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
public class MaintAuditSubgroupId implements Serializable {

    private Integer legalEntityCode;

    private String auditTypeCode;

    private String auditGroupCode;

    private String auditSubGroupCode;
}
