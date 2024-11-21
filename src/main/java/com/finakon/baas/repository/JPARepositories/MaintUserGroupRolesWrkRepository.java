package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintUsergroupRolesWrk;

@Repository
public interface MaintUserGroupRolesWrkRepository extends JpaRepository<MaintUsergroupRolesWrk, Integer> {

    List<MaintUsergroupRolesWrk> findByLegalEntityCode(Integer legalEntityCode);
    
    List<MaintUsergroupRolesWrk> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
}