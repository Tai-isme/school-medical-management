package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.LoginResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.OtpSentResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.Student;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.service.JwtService;
import com.swp391.school_medical_management.service.OtpService;
import com.swp391.school_medical_management.service.TwilioService;
import org.aspectj.bridge.Message;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Object login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone()).orElseThrow(() ->
                new BadCredentialsException("Phone not matches!"));


        String phone = request.getPhone();
        String otp = otpService.generateOtp();

        // Save otp to Redis
        String redisKey = "otp:" + phone;
        redisTemplate.opsForValue().set(redisKey, otp, 1, TimeUnit.MINUTES);
        // Send otp to phone
        twilioService.sendOtp(phone, otp);

//        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
//
//        List<Student> studentList = studentRepository.findByParent_Id(userDTO.getId());
//
//        List<StudentDTO> studentDTOList = studentList.stream().map(student
//                -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
//
//        String token = jwtService.generateToken(userDTO.getId(), userDTO.getRole());
//
//        return new LoginResponse(token, userDTO, studentDTOList);
        return ResponseEntity.ok(new OtpSentResponse("Da gui OTP den dien thoai. Hay xac nhan!"));
    }


}
