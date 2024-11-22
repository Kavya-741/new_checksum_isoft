package com.bankaudit.jwthelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.AntPathMatcher;

public class AuthorizationHelper {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static Map<String, List<String>> endpointsWithPermissions = new HashMap<>();
    private static String ADD = "Add";
    private static String EDIT = "Edit";
    private static String VIEW = "View";
    private static String AUTHORIZE = "Authorize";
    private static String DELETE ="Delete";
    private static String CERTIFY =  "Certify";
    private static String FILEUPLOAD =  "FileUpload";
    private static String FILEVIEW = "FileView";

    //add apis that need to be authorized
    private static final List<String> apisToBeAuthorizedWithTemporaryToken = Arrays.asList("api/users/logout/", "api/users/updateTokenDetails");
    private static final List<String> apisWithPathVariables = Arrays.asList();


    public final static Map<String, List<String>> getPermission() {
        return endpointsWithPermissions;

    }

    static final boolean isToBeAuthorizedEndpointWithTemporaryToken(String requestPath) {
        for (String apiToBeAuthorized : apisToBeAuthorizedWithTemporaryToken) {
            if (pathMatcher.match(apiToBeAuthorized, requestPath)) {
                return true;
            }
        }
        return false;
    }

    static final String getApiWithPathVariable(String requestPath) {
        for (String apiWithPathVariable : apisWithPathVariables) {
            if (pathMatcher.match(apiWithPathVariable, requestPath)) {
                return apiWithPathVariable;
            }
        }
        return null;
    }
}
