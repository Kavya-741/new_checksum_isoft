package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintUsergroupRoles;

@Repository
public interface MaintUserGroupRolesRepository extends JpaRepository<MaintUsergroupRoles, Integer> {

    MaintUsergroupRoles findByLegalEntityCodeAndUgRoleCode(Integer legalEntityCode, String ugRoleCode);

    List<MaintUsergroupRoles> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
    
}