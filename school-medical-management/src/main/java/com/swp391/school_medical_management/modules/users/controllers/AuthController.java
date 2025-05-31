package com.swp391.school_medical_management.modules.users.controllers;

import java.util.Optional;

import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.IdTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.RefreshTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.RefreshTokenDTO;
import com.swp391.school_medical_management.modules.users.entities.RefreshToken;
import com.swp391.school_medical_management.modules.users.repositories.RefreshTokenRepository;
import com.swp391.school_medical_management.modules.users.services.impl.BlacklistService;
import com.swp391.school_medical_management.modules.users.services.impl.UserService;
import com.swp391.school_medical_management.service.JwtService;

import jakarta.validation.Valid;

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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // @PostMapping("/active")
    // public ResponseEntity<?> active(@Valid @RequestBody ActiveRequest request) {
    //     userService.activeAccount(request);
    //     return ResponseEntity.ok(new ApiResponse<>(true, "Account activated. Check your email.", null));
    // }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<LoginResponse> response = userService.authenticate(request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/blacklisted")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        try {
            blacklistService.create(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Token blacklisted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        logger.info("logout");
        try {
            String token = bearerToken.substring(7);
            BlacklistTokenRequest request = new BlacklistTokenRequest();
            request.setToken(token);
            blacklistService.create(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Token logged out", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        }
        Optional<RefreshToken> dbRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (dbRefreshToken.isPresent()) {
            Long userId = dbRefreshToken.get().getUser().getId();
            String email = dbRefreshToken.get().getUser().getEmail();
            String phone = dbRefreshToken.get().getUser().getPhone();
            String role = dbRefreshToken.get().getUser().getRole();
            String newToken = jwtService.generateToken(userId, email, phone, role);
            String newRefreshToken = jwtService.generateRefreshToken(userId);
            return ResponseEntity.ok(new RefreshTokenDTO(newToken, newRefreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Refresh token not valid", null));
    }


    @PostMapping("/phone")
    public ResponseEntity<?> loginWithPhone(@RequestBody IdTokenRequest request) {
        logger.info("phone-login");
        ApiResponse<LoginResponse> response = userService.phoneLogin(request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody IdTokenRequest request) {
        ApiResponse<LoginResponse> response = userService.googleLogin(request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userRepository.findUserById(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("User khong ton tai"));
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
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

