package com.finakon.baas.repository.JPARepositories;

import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserId;
import com.finakon.baas.entities.UserWrk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWrkRepository extends JpaRepository<UserWrk, UserId> {

}