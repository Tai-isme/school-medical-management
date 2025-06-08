package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.services.impl.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.RefreshTokenDTO;
import com.swp391.school_medical_management.modules.users.services.impl.BlacklistService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/nurses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNurseAccount(@Valid @RequestBody NurseAccountRequest request) {
        UserDTO userDTO = authService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
            LoginResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/blacklisted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
            blacklistService.create(request);
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
            String token = bearerToken.substring(7);
            BlacklistTokenRequest request = new BlacklistTokenRequest();
            request.setToken(token);
            blacklistService.create(request);
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenDTO> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
            RefreshTokenDTO response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/phone")
    public ResponseEntity<LoginResponse> loginWithPhone(@Valid @RequestBody IdTokenRequest request) {
        LoginResponse response = authService.phoneLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@Valid @RequestBody IdTokenRequest request) {
        LoginResponse response = authService.googleLogin(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = (UserEntity) userRepository.findUserByUserId(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("User khong ton tai"));
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .address(user.getAddress())
                .password(user.getPassword())
                .build();
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", userDTO));
    }
}

