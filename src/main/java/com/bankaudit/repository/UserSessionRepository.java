package com.bankaudit.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankaudit.model.UserSession;
import com.bankaudit.model.UserSessionId;

@Repository("userSessionRepository")
public interface UserSessionRepository extends JpaRepository<UserSession, UserSessionId> {

	public UserSession findByUserIdAndLegalEntityCodeAndIsLocked(String userId, Integer legalEntityCode, boolean isLocked);

	public UserSession findByUserIdAndLegalEntityCodeAndIsLogged(String userId, Integer legalEntityCode, boolean isLoggedIn);


	@Modifying
	@Transactional
	@Query(value = "UPDATE users_session_details us  SET us.is_logged_in=?1, us.token = NULL where us.user_id= ?2 and us.legal_entity_code= ?3  ", nativeQuery = true)
	public void updateUserLogoutSession(boolean isLogged, String userId, int legalEntityCode);

	@Modifying
	@Transactional
	@Query(value = "UPDATE users_session_details SET token = ?1 WHERE user_id = ?2 and legal_entity_code = ?3", nativeQuery = true)
	void updateToken(String token, String userId, Integer legalEntityCode);

	UserSession findByUserIdAndLegalEntityCode(String userId,Integer legalEntityCode);
}