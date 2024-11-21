package com.finakon.baas.repository.JPARepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintEntity;

@Repository
public interface MaintEntityRepository extends JpaRepository<MaintEntity, Integer> {

    MaintEntity findByLegalEntityCodeAndUnitCode(Integer legalEntityCode, String unitCode);

    List<MaintEntity> findByLegalEntityCodeAndEntityStatus(Integer legalEntityCode, String entityStatus);
    
}
