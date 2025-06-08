package com.swp391.school_medical_management.helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391.school_medical_management.service.JwtService;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/active")
                || path.startsWith("/api/auth/blacklisted")
                || path.startsWith("/api/auth/refresh")
                || path.startsWith("/api/auth/phone")
                || path.startsWith("/api/auth/google")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/api/test") // Example endpoint for testing
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;
        final String role;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Khong tim thay token");
            return;
        }

        jwt = authHeader.substring(7);

        if (!jwtService.isTokenFormatValid(jwt)) {
            sendErrorResponse(response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Token khong dung dinh dang");
            return;
        }

        if (jwtService.isTokenExpired(jwt)) {
            sendErrorResponse(
                    response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Token da het han");
            return;
        }

        if (!jwtService.isSignatureValid(jwt)) {
            sendErrorResponse(response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Chu ky khong hop le");
            return;
        }

        if (!jwtService.isIssuerToken(jwt)) {
            sendErrorResponse(response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Nguon token khong hop le");
            return;
        }

        if(jwtService.isBlackListedToken(jwt)){
            logger.info("Blacklisted token");
            sendErrorResponse(response,
                    request,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Xac thuc khong thanh cong!",
                    "Token bi khoa");
            return;
        }

        userId = jwtService.getUserIdFromJwt(jwt);
        role = jwtService.getRoleFromJwt(jwt);
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(
            @Nonnull HttpServletResponse response,
            @Nonnull HttpServletRequest request,
            int statusCode,
            String error,
            String message
    ) throws java.io.IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
