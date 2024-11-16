package com.finakon.baas.repository;

import com.finakon.baas.entities.User;
import com.finakon.baas.entities.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {
        // Define any custom query methods here

        @Query(value = "SELECT * FROM user WHERE legal_entity_code = :legalEntityCode AND " +
                        "(user_id = :loginId) AND password = SHA2(:password, 256) AND " +
                        "status = 'A' AND entity_status = 'A'", nativeQuery = true)
        User findByLegalEntityCodeAndLoginIdAndPasswordAndStatusAndEntityStatus(
                        @Param("legalEntityCode") Integer legalEntityCode,
                        @Param("loginId") String loginId, @Param("password") String password);

    @Query("SELECT userId FROM User u WHERE u.legalEntityCode = :legalEntityCode AND u.userId = :userId AND  u.status = 'A' AND u.entityStatus = 'A'")
    List<String> findByUserId(@Param("legalEntityCode") Integer legalEntityCode, @Param("userId") String userId);

    User findByUserIdAndLegalEntityCodeAndStatusAndEntityStatus(String userId, Integer legalEntityCode, String status, String entityStatus);



}