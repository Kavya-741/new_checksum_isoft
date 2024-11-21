package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.UserRoleMappingHst;

@Repository
public interface UserRoleMappingHstRepository extends JpaRepository<UserRoleMappingHst, Integer> {

    List<UserRoleMappingHst> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
}