package com.finakon.baas.repository.JPARepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finakon.baas.entities.MaintAddress;

@Repository
public interface MaintAddressRepository extends JpaRepository<MaintAddress, Integer>{
}
