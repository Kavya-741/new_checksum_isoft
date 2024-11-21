package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.finakon.baas.entities.UserRoleMapping;
import com.finakon.baas.entities.UserRoleMappingWrk;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Integer>{

    @Query(value = "SELECT user_role_id FROM user_role_mapping WHERE user_id = :userId and legal_entity_code = :legalEntityCode", nativeQuery = true)
    List<String> findRolesByUserId(String userId, Integer legalEntityCode);

    List<UserRoleMapping> findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(String userId, Integer legalEntityCode, String status, String entityStatus);

    List<UserRoleMapping> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);

    List<UserRoleMapping> findByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId);
    
}
