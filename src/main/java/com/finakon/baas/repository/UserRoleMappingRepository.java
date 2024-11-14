package com.finakon.baas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.finakon.baas.entities.UserRoleMapping;

@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, Integer>{

    @Query(value = "SELECT user_role_id FROM user_role_mapping WHERE user_id = :userId and legal_entity_code = :legalEntityCode", nativeQuery = true)
    List<String> findRolesByUserId(String userId, Integer legalEntityCode);
    
}
