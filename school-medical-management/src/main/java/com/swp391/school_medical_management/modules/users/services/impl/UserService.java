package com.swp391.school_medical_management.modules.users.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.request.IdTokenRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.Student;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.JwtService;
import com.swp391.school_medical_management.service.PasswordService;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    public ApiResponse<LoginResponse> authenticate(LoginRequest request) {
        try {
            User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(() ->
                    new BadCredentialsException("Incorrect email or password!"));
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
                throw new BadCredentialsException("Incorrect email or password!");
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<Student> studentList = studentRepository.findByParentId(userDTO.getId());
            String token = jwtService.generateToken(user.getId(),user.getEmail(), user.getPhone(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getId());
            List<StudentDTO> studentDTOList = studentList.stream().map(student
                    -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            LoginResponse loginResponse = new LoginResponse(token, refreshToken, userDTO, studentDTOList);
            return new ApiResponse<>(true, "Login successful", loginResponse);
        } catch (BadCredentialsException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    // public void activeAccount(ActiveRequest request) {
    //     User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(() ->
    //             new BadCredentialsException("Email not found!"));
    //     String password = passwordService.generateStrongRandomPassword();
    //     String encodedPassword = passwordEncoder.encode(password);
    //     user.setPassword(encodedPassword);
    //     if (user.is_activated()) throw new RuntimeException("Account already activated");
    //     emailService.sendEmail(request.getEmail(), "Account activation password",
    //             "Hi,\n\nYour temporary password is: " + password
    //                     + "\nPlease change after login.\n\nThanks.");
    //     user.set_activated(true);
    //     userRepository.save(user);
    // }

    public ApiResponse<LoginResponse> phoneLogin(IdTokenRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String phoneNumberINTL = (String) decodedToken.getClaims().get("phone_number");
            if(phoneNumberINTL.startsWith("+84")) phoneNumberINTL = "0" + phoneNumberINTL.substring(3);
            User user = userRepository.findUserByPhone(phoneNumberINTL).orElseThrow(() ->
                    new BadCredentialsException("Not found user with phone number !"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<Student> studentList = studentRepository.findByParentId(userDTO.getId());
            String token = jwtService.generateToken(user.getId(),user.getEmail(), user.getPhone(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getId());
            List<StudentDTO> studentDTOList = studentList.stream().map(student
                    -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            LoginResponse loginResponse = new LoginResponse(token, refreshToken, userDTO, studentDTOList);
            return new ApiResponse<>(true, "Login successfull", loginResponse);
        } catch (FirebaseAuthException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public ApiResponse<LoginResponse> googleLogin(IdTokenRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String email = decodedToken.getEmail();
            User user = userRepository.findUserByEmail(email).orElseThrow(() ->
                    new BadCredentialsException("Not found user with email !"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<Student> studentList = studentRepository.findByParentId(userDTO.getId());
            String token = jwtService.generateToken(user.getId(),user.getEmail(), user.getPhone(), user.getRole());
            String refreshToken = jwtService.generateRefreshToken(user.getId());
            List<StudentDTO> studentDTOList = studentList.stream().map(student
                    -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            LoginResponse loginResponse = new LoginResponse(token, refreshToken, userDTO, studentDTOList);
            return new ApiResponse<>(true, "Login successfull", loginResponse);
        } catch (FirebaseAuthException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }
}
