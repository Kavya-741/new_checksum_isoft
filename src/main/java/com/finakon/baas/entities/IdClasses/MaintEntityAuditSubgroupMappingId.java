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
public class MaintEntityAuditSubgroupMappingId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer legalEntityCode;

    private String mappingType;

    private String id;

    private String auditTypeCode;

    private String auditGroupCode;

    private String auditSubGroupCode;
}
