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
public class UserId implements Serializable{
    private Integer legalEntityCode;
    private String userId;
    private String status;

    // Constructors, getters, and setters
}