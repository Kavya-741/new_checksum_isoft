package com.finakon.baas.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class UserRoleMappingId implements Serializable {

    private Integer legalEntityCode;

    private String userRoleId;

     // Getters, Setters, equals, and hashCode
     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         UserRoleMappingId that = (UserRoleMappingId) o;
         return Objects.equals(legalEntityCode, that.legalEntityCode) &&
                Objects.equals(userRoleId, that.userRoleId);
     }
 
     @Override
     public int hashCode() {
         return Objects.hash(legalEntityCode, userRoleId);
     }

}