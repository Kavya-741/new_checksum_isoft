package com.bankaudit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankaudit.model.MaintLegalEntity;

@Repository
public interface MaintLegalEntityRepository extends JpaRepository<MaintLegalEntity, Integer> {
    @Query(value = "SELECT * FROM maint_legal_entity WHERE url = ?1 and status = 'A'", nativeQuery = true)
    MaintLegalEntity findLegalEnityCodeByDomain(String domain);

    MaintLegalEntity findByLegalEntityCode(Integer legalEntityCode);

}