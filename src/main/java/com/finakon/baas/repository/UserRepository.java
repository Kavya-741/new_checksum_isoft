package com.finakon.baas.repository;

import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, UserId> {
        // Define any custom query methods here

        @Query("SELECT userId FROM User u WHERE u.legalEntityCode = :legalEntityCode AND " +
                        "(u.userId = :loginId OR u.emailId = :loginId) AND u.password = SHA2(:password, 256) AND " +
                        "u.status = 'A' AND u.entityStatus = 'A'")
        List<String> countByLegalEntityCodeAndLoginIdAndPasswordAndStatusAndEntityStatus(
                        @Param("legalEntityCode") Integer legalEntityCode,
                        @Param("loginId") String loginId, @Param("password") String password);

    @Query("SELECT userId FROM User u WHERE u.legalEntityCode = :legalEntityCode AND u.userId = :userId AND  u.status = 'A' AND u.entityStatus = 'A'")
    List<String> findByUserId(@Param("legalEntityCode") Integer legalEntityCode, @Param("userId") String userId);

    User findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(String userId, Integer legalEntityCode, String status, String entityStatus);



}