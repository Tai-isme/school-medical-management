


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

import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
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
                logger.warn("Missing or invalid Authorization header. Allowing connection without auth.");
                return message; 
            }

            String token = jwt.substring(7);

            if (!jwtService.isTokenFormatValid(token)) {
                logger.warn("Invalid JWT format");
                return message;
            }

            if (!jwtService.isSignatureValid(token)) {
                logger.warn("Invalid JWT signature");
                return message;
            }

            if (jwtService.isTokenExpired(token)) {
                logger.warn("JWT token expired");
                return message;
            }

            if (jwtService.isBlackListedToken(token)) {
                logger.warn("JWT token is blacklisted");
                return message;
            }

            String userId = jwtService.getUserIdFromJwt(token);
            UserRole role = jwtService.getRoleFromJwt(token);

            if (userId == null || role == null) {
                logger.warn("Invalid user information in JWT");
                return message;
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            accessor.setUser(auth);

            logger.info("WebSocket authentication successful for user ID: {} with role: {}", userId, role);

        } catch (Exception e) {
            logger.error("Unexpected error during WebSocket authentication: {}", e.getMessage(), e);
            return message;
        }
    }

    return message;
}
}