package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.service.JwtService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/parent")
public class ParentController {
    private static final Logger logger = LoggerFactory.getLogger(ParentController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @GetMapping("data")
    public ResponseEntity<?> parent() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("User khong ton tai"));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", userDTO));
    }
}
