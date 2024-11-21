package com.finakon.baas.jwthelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finakon.baas.entities.MaintLegalEntity;
import com.finakon.baas.entities.UserSession;
import com.finakon.baas.helper.DomainUtil;
import com.finakon.baas.repository.JPARepositories.UserRepository;
import com.finakon.baas.repository.JPARepositories.UserSessionRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    public JwtTokenFilter(UserSessionRepository userSessionRepository, UserRepository userRepository) {
        this.userSessionRepository = userSessionRepository;
        this.userRepository = userRepository;
    }

    @Value("${isdev}")
    private boolean isDev;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final List<String> excludedEndpoints = Arrays.asList("/user/healthCheck", "/user/login",
            "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**",
            "/api/users/forgot-password", "/api/users/validate-otp", "/api/roles");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath;
        requestPath = request.getRequestURI().substring(request.getContextPath().length());

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            if (isExcludedEndpoint(requestPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Authorization header is missing or invalid");
                return;
            }
            String token = authorizationHeader.substring(7);
            MaintLegalEntity maintLegalEntity = DomainUtil.getLegalEntityCodeByDomain(request.getHeader("Host"));

            if (!JwtTokenUtil.validateJwt(token, maintLegalEntity)) {
                String message = "Your session has expired please re-login again";
                Map<String, Object> responseMap = new HashMap<>();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                responseMap.put("success", false);
                responseMap.put("message", message);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(responseMap);
                response.getWriter().write(jsonResponse);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                return;
            }
            String userId = JwtTokenUtil.getUserIdFromJwt(token);
            String legalEntityCode = JwtTokenUtil.getLeCodeFromJwt(token);
            UserSession userDetails = userSessionRepository.findByUserIdAndLegalEntityCode(userId, Integer.parseInt(legalEntityCode));

            if (JwtTokenUtil.getUserIdFromJwt(token) == null || userDetails == null) {
                String message = "Not a valid session";
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("success", false);
                responseMap.put("message", message);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(responseMap);
                response.getWriter().write(jsonResponse);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                return;
            }

            if(AuthorizationHelper.isToBeAuthorizedEndpointWithTemporaryToken(requestPath)){
                if (!JwtTokenUtil.getUserIdFromJwt(token).equals(userId)) {
                    String message = "Not a valid session";
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", false);
                    responseMap.put("message", message);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = objectMapper.writeValueAsString(responseMap);
                    response.getWriter().write(jsonResponse);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }

    }

    private boolean isExcludedEndpoint(String requestPath) {
        return excludedEndpoints.stream()
                .anyMatch(excludedPath -> pathMatcher.match(excludedPath, requestPath));
    }

}