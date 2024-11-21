package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintAuditSubgroup;

@Repository
public interface MaintAuditSubgroupRepository extends JpaRepository<MaintAuditSubgroup, Integer> {

    @Query(value = "SELECT m FROM maint_audit_subgroup m WHERE (u.legal_entity_code = :legalEntityCode) " +
            "AND (:auditTypeCode IS NULL OR u.audit_type_code = :auditTypeCode) " +
            "AND (:auditGroupCode IS NULL OR u.audit_group_code = :auditGroupCode) " +
            "AND (:auditSubGroupCode IS NULL OR u.audit_sub_group_code = :auditSubGroupCode) " +
            "AND (u.entity_status = :entityStatus)", nativeQuery = true)
    List<MaintAuditSubgroup> findByMatchingProperties(Integer legalEntityCode, String auditTypeCode,
            String auditGroupCode,
            String auditSubGroupCode, String entityStatus);
}
