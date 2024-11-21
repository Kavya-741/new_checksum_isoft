package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.UserDeptMappingHst;

@Repository
public interface UserDeptMappingHstRepository extends JpaRepository<UserDeptMappingHst, Integer> {
    List<UserDeptMappingHst> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
}