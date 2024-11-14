package com.finakon.baas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.GeneralParameter;
import com.finakon.baas.entities.MaintLegalEntity;

@Repository
public interface GeneralParameterRepository extends JpaRepository<GeneralParameter, Integer>{
    GeneralParameter findByLegalEntityCodeAndKey1AndKey2(Integer legalEntityCode, String key1, String key2);
}
