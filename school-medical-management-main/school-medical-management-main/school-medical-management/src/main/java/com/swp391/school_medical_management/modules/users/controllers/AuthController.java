package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.request.ActiveRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.services.impl.BlacklistService;
import com.swp391.school_medical_management.modules.users.services.impl.UserService;
import com.swp391.school_medical_management.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("active")
    public ResponseEntity<?> active(@Valid @RequestBody ActiveRequest request) {
        userService.activeAccount(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account activated. Check your email.", null));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<LoginResponse> response = userService.authenticate(request);
        if(response.isSuccess()) return ResponseEntity.ok(response);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("blacklisted_tokens")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        try {
             blacklistService.create(request);
            return ResponseEntity.ok(new ApiResponse<>(true,"Token blacklisted successfully",null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
    @GetMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            BlacklistTokenRequest request = new BlacklistTokenRequest();
            request.setToken(token);
            blacklistService.create(request);
            return ResponseEntity.ok(new ApiResponse<>(true,"Token logged out",null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false,e.getMessage(),null));
        }
    }
}

