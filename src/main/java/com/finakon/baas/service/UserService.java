package com.finakon.baas.service;

import org.springframework.http.ResponseEntity;

import com.finakon.baas.dto.LoginRequest;
import com.finakon.baas.dto.LogoutRequest;
import com.finakon.baas.dto.UpdateTokenDetailsRequest;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.User;

public interface UserService {

    ResponseEntity<Object> login(MaintLegalEntity maintLegalEntity, LoginRequest loginRequest);

    ResponseEntity<Object> logout(MaintLegalEntity maintLegalEntity, LogoutRequest loginRequest);

    ResponseEntity<Object> updateTokenDetails(MaintLegalEntity maintLegalEntity, UpdateTokenDetailsRequest updateToken);

    String validateCredentials(Integer legalEntityCode, String username, String password);

    boolean validateCaptcha(String captcha);

    void updateUserLogoutSession(Integer legalEntityCode, String userId);

    Boolean isUserLocked(String userId, Integer legalEntityCode, boolean isLoked);

    Boolean isUserAlreadyLoggedIn(Integer legalEntityCode, String userId, Integer isLoggedIn);

    Boolean updateUserSession(Integer legalEntityCode, String userId);

    User getLoggedInUser(String userId,Integer legalEntityCode);

    User createUserSession(String userId, Integer legalEntityCode);

    


}
