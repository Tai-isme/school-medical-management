package com.swp391.school_medical_management.modules.users.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp391.school_medical_management.modules.users.dtos.request.NurseAccountRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.RefreshTokenDTO;
import com.swp391.school_medical_management.modules.users.entities.RefreshTokenEntity;
import com.swp391.school_medical_management.modules.users.repositories.RefreshTokenRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swp391.school_medical_management.modules.users.dtos.request.IdTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.JwtService;
import com.swp391.school_medical_management.service.PasswordService;
import org.springframework.web.server.ResponseStatusException;

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
    private PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public LoginResponse authenticate(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findUserByEmail(request.getEmail());
        if(userOpt.isEmpty())
            throw new BadCredentialsException("Incorrect email or password!");
        UserEntity user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Incorrect email or password!");
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getPhone(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getUserId());
        return new LoginResponse(token, refreshToken, userDTO, null);
    }

    public UserDTO createAccount(NurseAccountRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        String password = passwordService.generateStrongRandomPassword();
        String encodedPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setFullName(request.getName());
        user.setPassword(encodedPassword);
        user.setRole("NURSE");
        userRepository.save(user);
        emailService.sendEmail(request.getEmail(), "Account: ",
                "\nEmail: " + request.getEmail()
                        + "\nPassword: " + password);
        return modelMapper.map(user, UserDTO.class);
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
            if(userOpt.isEmpty())
                throw new BadCredentialsException("Not found user with phone number: " + phoneNumberINTL);
            UserEntity user = userOpt.get();
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<StudentEntity> studentList = studentRepository.findStudentByParent_UserId(userDTO.getId());
            String token = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getPhone(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getUserId());
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
            if(userOpt.isEmpty())
                throw new BadCredentialsException("Not found user with email: " + email);
            UserEntity user = userOpt.get();
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<StudentEntity> studentList = studentRepository.findStudentByParent_UserId(userDTO.getId());
            String token = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getPhone(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getUserId());
            List<StudentDTO> studentDTOList = studentList.stream()
                    .map(student -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            return new LoginResponse(token, refreshToken, userDTO, studentDTOList);
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }
}