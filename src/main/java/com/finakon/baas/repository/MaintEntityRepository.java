package com.finakon.baas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintEntity;

@Repository
public interface MaintEntityRepository extends JpaRepository<MaintEntity, Integer> {

    MaintEntity findByLegalEntityCodeAndUnitCode(Integer legalEntityCode, String unitCode);
    
}
