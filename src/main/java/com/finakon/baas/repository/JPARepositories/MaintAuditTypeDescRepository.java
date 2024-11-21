package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintAuditTypeDesc;

@Repository
public interface MaintAuditTypeDescRepository extends JpaRepository<MaintAuditTypeDesc, Integer> {

    List<MaintAuditTypeDesc> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
}
