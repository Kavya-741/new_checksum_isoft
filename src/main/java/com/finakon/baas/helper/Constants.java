package com.finakon.baas.helper;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String MESSAGE = "message";
    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String ACCESS_TOKEN_TIMEOUT = "ACCESS_TOKEN_TIMEOUT";

    public class UserControllerErrorCode {
        public static final String INVALID_DOMAIN = "Invalid domain";
        public static final String INVALID_CAPTCHA = "Invalid Captcha";
        public static final String ACCOUNT_LOCKED = "Your account is locked please reset password";
        public static final String EXISTING_SESSION = "Are you wanting to logout the user who is already logged into another system ?";
        public static final String LOGIN_SUCCESS = "Logged in successfully";
        public static final String LOGIN_EXCEPTION = "Login Exception";
        public static final String LOGOUT_SUCCESS = "Successfully logged out";
        public static final String LOGOUT_EXCEPTION = "Logout Eception";
        public static final String INVALID_ROLE = "Invalid User Role";
        public static final String TOKEN_DETAILS_SUCCESS = "Successfully updated token details";

        public static Map<String, String> userStatusDesc;
        static {
            userStatusDesc = new HashMap<>();
            userStatusDesc.put("I", "InActive");
            userStatusDesc.put("C", "Closed");
            userStatusDesc.put("L", "Locked");
        }

    }

}
