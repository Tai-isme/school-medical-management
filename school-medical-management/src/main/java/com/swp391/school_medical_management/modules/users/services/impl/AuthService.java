package com.swp391.school_medical_management.modules.users.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swp391.school_medical_management.modules.users.dtos.request.BlacklistTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.ChangePasswordRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.IdTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.UpdateProfileRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.RefreshTokenDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.ClassEntity;
import com.swp391.school_medical_management.modules.users.entities.RefreshTokenEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.repositories.RefreshTokenRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.service.JwtService;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BlacklistService blacklistService;

    public LoginResponse authenticate(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findUserByEmail(request.getEmail());
        if (userOpt.isEmpty())
            throw new BadCredentialsException("Incorrect email or password!");
        UserEntity user = userOpt.get();
        if (!user.isActive()) {
            throw new BadCredentialsException("Your account is not active!");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Incorrect email or password!");
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        String token = jwtService.generateToken(userDTO.getId(), userDTO.getEmail(), userDTO.getPhone(),
                userDTO.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());
        return new LoginResponse(token, refreshToken, userDTO, null);
    }

    public void updateAccountStatus(int userId, boolean status) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        UserEntity user = userOpt.get();
        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot disable ADMIN account");
        }
        user.setActive(status);
        userRepository.save(user);
    }

    @Transactional
    public void logout(String bearerToken) {
        logger.info("logout");

        String token = bearerToken.substring(7); // Bỏ "Bearer "

        // 1. Đưa vào blacklist
        BlacklistTokenRequest request = new BlacklistTokenRequest();
        request.setToken(token);
        blacklistService.create(request);

        // 2. Xoá refresh token theo userId
        String userId = jwtService.getUserIdFromJwt(token);
        Optional<UserEntity> userOpt = userRepository.findById(Integer.parseInt(userId));
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        UserEntity user = userOpt.get();
        refreshTokenRepository.deleteByUser(user);
    }

    public void changePassword(int userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void updateProfile(int userId, UpdateProfileRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setFullName(request.getFullName());
        // if (!user.getEmail().equals(request.getEmail())) {
        //     boolean emailExists = userRepository.existsByEmail(request.getEmail());
        //     if (emailExists) {
        //         throw new RuntimeException("Email already in use");
        //     }
        user.setEmail(request.getEmail());
        // }
        // if (!user.getPhone().equals(request.getPhone())) {
        //     boolean phoneExists = userRepository.existsByPhone(request.getPhone());
        //     if (phoneExists) {
        //         throw new RuntimeException("Phone already in use");
        //     }
        user.setPhone(request.getPhone());
        // }
        user.setAddress(request.getAddress());

        userRepository.save(user);
    }

    public RefreshTokenDTO refreshToken(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token!");
        }
        RefreshTokenEntity dbToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found!"));
        UserEntity user = dbToken.getUser();
        String newToken = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getPhone(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUserId());

        return new RefreshTokenDTO(newToken, newRefreshToken);
    }

    public LoginResponse phoneLogin(IdTokenRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String phoneNumberINTL = (String) decodedToken.getClaims().get("phone_number");
            if (phoneNumberINTL.startsWith("+84"))
                phoneNumberINTL = "0" + phoneNumberINTL.substring(3);
            Optional<UserEntity> userOpt = userRepository.findUserByPhone(phoneNumberINTL);
            if (userOpt.isEmpty()) {
                throw new BadCredentialsException("Not found user with phone number: " + phoneNumberINTL);
            }
            UserEntity user = userOpt.get();
            if (!user.isActive()) {
                throw new BadCredentialsException("Your account is not active!");
            }
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<StudentEntity> studentList = studentRepository.findStudentByParent_UserId(userDTO.getId());
            String token = jwtService.generateToken(userDTO.getId(), userDTO.getEmail(), userDTO.getPhone(),
                    userDTO.getRole());
            String refreshToken = jwtService.generateRefreshToken(userDTO.getId());
            List<StudentDTO> studentDTOList = studentList.stream()
                    .map(student -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            return new LoginResponse(token, refreshToken, userDTO, studentDTOList);
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public LoginResponse googleLogin(IdTokenRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String email = decodedToken.getEmail();
            Optional<UserEntity> userOpt = userRepository.findUserByEmail(email);
            if (userOpt.isEmpty())
                throw new BadCredentialsException("Not found user with email: " + email);

            UserEntity user = userOpt.get();
            if (!user.isActive()) {
                throw new BadCredentialsException("Your account is not active!");
            }

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);

            List<StudentEntity> studentList = studentRepository.findStudentByParent_UserId(userDTO.getId());

            List<StudentDTO> studentDTOList = studentList.stream().map(student -> {
                StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

                if (student.getClassEntity() != null) {
                    ClassEntity classEntity = student.getClassEntity();
                    ClassDTO classDTO = new ClassDTO();
                    classDTO.setClassId(classEntity.getClassId());
                    classDTO.setTeacherName(classEntity.getTeacherName());
                    classDTO.setClassName(classEntity.getClassName());
                    classDTO.setQuantity(classEntity.getQuantity());
                    studentDTO.setClassDTO(classDTO);
                }

                return studentDTO;
            }).collect(Collectors.toList());

            userDTO.setStudentDTOs(studentDTOList);

            String token = jwtService.generateToken(userDTO.getId(), userDTO.getEmail(), userDTO.getPhone(),
                    userDTO.getRole());
            String refreshToken = jwtService.generateRefreshToken(userDTO.getId());

            System.out.println("=== NEW JWT TOKEN ===");
            Date exp = jwtService.extractExpiration(token);
            System.out.println("Generated token: " + token);
            System.out.println("Expires at: " + exp);
            System.out.println("Now: " + new Date());
            System.out.println("=====================");

            return new LoginResponse(token, refreshToken, userDTO, studentDTOList);
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

}