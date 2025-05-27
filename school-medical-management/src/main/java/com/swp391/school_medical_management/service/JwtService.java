package com.swp391.school_medical_management.service;

import com.swp391.school_medical_management.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private JwtConfig jwtConfig;
    private Key key;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecretKey()));
    }

    public String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());
        return Jwts.builder()
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .claim("role", role)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String getRoleFromJwt(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }

    public boolean isTokenFormatValid(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            return tokenParts.length == 3;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSignatureValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token); // Nếu không throw là hợp lệ
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Key getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecretKey().getBytes();
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(keyBytes));
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }

    }

    public boolean isIssuerToken(String token) {
        String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
        return tokenIssuer.equals(jwtConfig.getIssuer());
    }


    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

}
