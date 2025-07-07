package com.swp391.school_medical_management.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.refreshSecret}")
    private String refreshSecret;
    @Value("${jwt.expiration}")
    private Long expirationTime;
    @Value("${jwt.expirationRefreshToken}")
    private Long expirationRefreshTime;
    @Value("${jwt.issuer}")
    private String issuer;
}
