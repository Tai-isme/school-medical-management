package com.swp391.school_medical_management.service;

import com.swp391.school_medical_management.config.JwtConfig;
import com.swp391.school_medical_management.modules.users.entities.RefreshTokenEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.repositories.BlacklistedTokenRepository;
import com.swp391.school_medical_management.modules.users.repositories.RefreshTokenRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtConfig jwtConfig;

    private SecretKey key;

    @Autowired
    private UserRepository userRepository;

    public JwtService(JwtConfig jwtConfig, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecretKey()));
    }

    public String generateToken(Long userId, String email, String phone, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());
        return Jwts.builder()
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("phone", phone)
                .claim("role", role)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationRefreshTime());

        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        UserEntity user = userRepository.findUserByUserId(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        Optional<RefreshTokenEntity> optionalRefreshToken = refreshTokenRepository.findByUser(user);
        if (optionalRefreshToken.isPresent()) {
            RefreshTokenEntity dBRefreshToken = optionalRefreshToken.get();
            dBRefreshToken.setRefreshToken(refreshToken);
            dBRefreshToken.setExpiryDate(localExpiryDate);
            refreshTokenRepository.save(dBRefreshToken);
        } else {
            RefreshTokenEntity insertToken = new RefreshTokenEntity();
            insertToken.setRefreshToken(refreshToken);
            insertToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            insertToken.setUser(user);
            refreshTokenRepository.save(insertToken);
        }
        return refreshToken;
    }

    public String getUserIdFromJwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Failed to extract user ID from JWT: {}", e.getMessage());
            return null;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("email", String.class);
        } catch (Exception e) {
            logger.error("Failed to extract email from JWT: {}", e.getMessage());
            return null;
        }
    }

    public UserRole getRoleFromJwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return UserRole.valueOf(claims.get("role", String.class));
        } catch (Exception e) {
            logger.error("Failed to extract role from JWT: {}", e.getMessage());
            return null;
        }
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
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
            return false;
        }
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("JWT expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    public boolean isIssuerToken(String token) {
        try {
            String tokenIssuer = getClaimFromToken(token, Claims::getIssuer);
            return tokenIssuer != null && tokenIssuer.equals(jwtConfig.getIssuer());
        } catch (Exception e) {
            logger.error("JWT issuer check failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Optional<RefreshTokenEntity> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(token);
            if (refreshTokenOpt.isEmpty()) {
                throw new RuntimeException("Refresh token not found");
            }
            RefreshTokenEntity refreshToken = refreshTokenOpt.get();
            LocalDateTime expirationLocalDateTime = refreshToken.getExpiryDate();
            Date expirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return expirationDate.after(new Date());
        } catch (Exception e) {
            logger.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Failed to parse JWT claims: {}", e.getMessage());
            return null;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    public boolean isBlackListedToken(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    public Authentication getAuthentication(String token) {
        String userId = getUserIdFromJwt(token);
        UserRole role = getRoleFromJwt(token);
        
        if (userId == null || role == null) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}