package com.bankaudit.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserSessionId implements Serializable {

    private Integer legalEntityCode;

    private String userId;

}