package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.UserDeptMapping;

@Repository
public interface UserDeptMappingRepository extends JpaRepository<UserDeptMapping, Integer> {

    List<UserDeptMapping> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);

    List<UserDeptMapping> findByLegalEntityCodeAndUserId(Integer legalEntityCode, String userId);
}