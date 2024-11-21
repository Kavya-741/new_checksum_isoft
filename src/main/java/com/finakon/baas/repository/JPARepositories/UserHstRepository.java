package com.finakon.baas.repository.JPARepositories;

import com.finakon.baas.entities.UserHst;
import com.finakon.baas.entities.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHstRepository extends JpaRepository<UserHst, UserId> {

}