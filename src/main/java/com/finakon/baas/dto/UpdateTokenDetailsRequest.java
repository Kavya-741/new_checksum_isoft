package com.finakon.baas.dto;

import lombok.Data;

@Data
public class UpdateTokenDetailsRequest {
    private String username;
    private String roleId;
}
