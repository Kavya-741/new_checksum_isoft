package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.UserDeptMappingWrk;

@Repository
public interface UserDeptMappingWrkRepository extends JpaRepository<UserDeptMappingWrk, Integer> {
    List<UserDeptMappingWrk> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);

    List<UserDeptMappingWrk> findByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId);
}