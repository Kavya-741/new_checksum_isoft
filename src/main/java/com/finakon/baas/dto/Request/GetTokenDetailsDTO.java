package com.finakon.baas.dto.Request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GetTokenDetailsDTO {
    private String userId;
    private Integer legalEntityCode;
    private String unitCode;
    private String parentUnitCode;
    private String roleCode;
    private LocalDateTime loginTime;
    private LocalDateTime tokenExpirationTime;
    private String firstName;
    private String lastName;
    private String middleName;
    private String roleName;
    private String levelCode;
    private String unitName;
}
