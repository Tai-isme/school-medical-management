package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.entities.BlacklistedTokenEntity;
import com.swp391.school_medical_management.modules.users.repositories.BlacklistedTokenRepository;
import com.swp391.school_medical_management.service.JwtService;
import io.jsonwebtoken.Claims; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class BlacklistService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtService jwtService;

    public void create(BlacklistTokenRequest request){
            if(blacklistedTokenRepository.existsByToken(request.getToken())){
                throw new IllegalStateException("Token already exists in blacklist");
            }
            Claims claims = jwtService.getAllClaimsFromToken(request.getToken());
            String userId = jwtService.getUserIdFromJwt(request.getToken());
            Date expiryDate = claims.getExpiration();
            BlacklistedTokenEntity blacklistedToken = new BlacklistedTokenEntity();
            blacklistedToken.setToken(request.getToken());
            blacklistedToken.setUserId(Long.parseLong(userId));
            blacklistedToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            blacklistedTokenRepository.save(blacklistedToken);
    }
}
