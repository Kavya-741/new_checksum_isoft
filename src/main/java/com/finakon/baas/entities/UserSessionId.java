package com.finakon.baas.entities;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserSessionId implements Serializable {

    private Integer legalEntityCode;

    private String userId;

}