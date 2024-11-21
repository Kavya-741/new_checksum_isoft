package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintEntityAuditSubgroupMapping;

@Repository
public interface MaintEntityAuditSubgroupMappingRepository
            extends JpaRepository<MaintEntityAuditSubgroupMapping, Integer> {

      @Query(value ="select m from maint_entity_audit_subgroup_mapping m WHERE (u.legal_entity_code = :legalEntityCode) " +
                  "AND (u.mapping_type = :mappingType) " +
                  "AND (u.id = :userId) " +
                  "AND (:audit_type_code IS NULL OR u.audit_type_code = :auditTypeCode)", nativeQuery = true)
      List<MaintEntityAuditSubgroupMapping> findByMatchingProperties(Integer legalEntityCode, String mappingType,
                  String userId, String auditTypeCode);
}
