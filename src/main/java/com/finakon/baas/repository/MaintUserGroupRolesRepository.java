package com.finakon.baas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintUsergroupRoles;

@Repository
public interface MaintUserGroupRolesRepository extends JpaRepository<MaintUsergroupRoles, Integer> {

    MaintUsergroupRoles findByLegalEntityCodeAndUgRoleCode(Integer legalEntityCode, String ugRoleCode);
    
}