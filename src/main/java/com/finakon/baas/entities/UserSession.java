package com.finakon.baas.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

import com.finakon.baas.entities.IdClasses.UserSessionId;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_session_details")
@IdClass(UserSessionId.class)
public class UserSession {

    @Id
    @Column(name = "legal_entity_code")
    private Integer legalEntityCode;
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_time_stamp")
    private Timestamp otpTimeStamp;

    @Column(name = "is_logged_in")
    private boolean isLogged;

    @Column(name = "last_signin")
    private Timestamp lastSignIn;

    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "number_of_attempts_to_login")
    private Integer numberOfAttemptsToLogin;

    @Column(name = "token")
    private String token;

    @Column(name = "ping_time")
    private Timestamp pingTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "otp_attempts")
    private Integer otpAttempts;

    @Column(name ="last_validated_ots")
    private Timestamp lastValidatedOtpTime;
}