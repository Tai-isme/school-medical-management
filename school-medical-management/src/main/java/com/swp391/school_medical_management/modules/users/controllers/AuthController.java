package com.swp391.school_medical_management.modules.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.ChangePasswordRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.IdTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.RefreshTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateProfileRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.RefreshTokenDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.services.impl.AuthService;
import com.swp391.school_medical_management.modules.users.services.impl.BlacklistService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.changePassword(Long.parseLong(userId), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PutMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateProfile(Long.parseLong(userId), request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thông tin cá nhân thành công", null));
    }

    @PostMapping("/blacklisted")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        blacklistService.create(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-account-status/{UserId}/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateAccountStatus(@PathVariable Long UserId, @PathVariable boolean status) {
        authService.updateAccountStatus(UserId, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        authService.logout(bearerToken);
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> me() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = (UserEntity) userRepository.findUserByUserId(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("User khong ton tai"));

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
