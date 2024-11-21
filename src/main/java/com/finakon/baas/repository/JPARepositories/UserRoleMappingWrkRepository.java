package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.UserRoleMappingWrk;

@Repository
public interface UserRoleMappingWrkRepository extends JpaRepository<UserRoleMappingWrk, Integer> {

    List<UserRoleMappingWrk> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);

    List<UserRoleMappingWrk> findByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId);

}