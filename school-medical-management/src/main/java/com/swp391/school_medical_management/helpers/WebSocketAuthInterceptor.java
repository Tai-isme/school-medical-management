package com.swp391.school_medical_management.helpers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;


import com.swp391.school_medical_management.service.JwtService;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    @Autowired
    private JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        logger.info("WebSocket message received. Command: {}", accessor.getCommand());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                String jwt = accessor.getFirstNativeHeader("Authorization");
                logger.info("JWT from header: {}", jwt != null ? "Present" : "Missing");

                if (jwt == null || !jwt.startsWith("Bearer ")) {
                    logger.error("Missing or invalid Authorization header");
                    throw new AccessDeniedException("Missing or invalid Authorization header");
                }

                String token = jwt.substring(7); // Remove "Bearer " prefix
                logger.info("Token extracted, length: {}", token.length());

                // Kiểm tra format
                if (!jwtService.isTokenFormatValid(token)) {
                    logger.error("Invalid JWT format");
                    throw new AccessDeniedException("Invalid JWT format");
                }

                // Kiểm tra signature
                if (!jwtService.isSignatureValid(token)) {
                    logger.error("Invalid JWT signature");
                    throw new AccessDeniedException("Invalid JWT signature");
                }

                // Kiểm tra expiration
                if (jwtService.isTokenExpired(token)) {
                    logger.error("JWT token expired");
                    throw new AccessDeniedException("JWT is expired");
                }

                // Kiểm tra blacklist
                if (jwtService.isBlackListedToken(token)) {
                    logger.error("JWT token is blacklisted");
                    throw new AccessDeniedException("JWT is blacklisted");
                }

                String userId = jwtService.getUserIdFromJwt(token);
                String role = jwtService.getRoleFromJwt(token);
                logger.info("User ID: {}, Role: {}", userId, role);

                if (userId == null || role == null) {
                    logger.error("Invalid user information in JWT");
                    throw new AccessDeniedException("Invalid user information in JWT");
                }

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                accessor.setUser(auth);
                
                logger.info("WebSocket authentication successful for user ID: {} with role: {}", userId, role);

            } catch (AccessDeniedException e) {
                logger.error("WebSocket authentication failed: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                logger.error("Unexpected error during WebSocket authentication: {}", e.getMessage(), e);
                throw new AccessDeniedException("Authentication failed: " + e.getMessage());
            }
        }

        return message;
    }
}