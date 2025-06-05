package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.request.ActiveRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.LoginRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgram;
import com.swp391.school_medical_management.modules.users.entities.Student;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgram;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineProgramRepository;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.JwtService;
import com.swp391.school_medical_management.service.PasswordService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @Autowired
    private HealthCheckProgramRepository healthRepo;

    @Autowired
    private VaccineProgramRepository vaccineRepo;


    public ApiResponse<LoginResponse> authenticate(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                    new BadCredentialsException("Incorrect email or password!"));
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
                throw new BadCredentialsException("Incorrect email or password!");
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<Student> studentList = studentRepository.findByParent_Id(userDTO.getId());
            String token = jwtService.generateToken(user.getEmail(), String.valueOf(user.getId()), user.getRole());
            List<StudentDTO> studentDTOList = studentList.stream().map(student
                    -> modelMapper.map(student, StudentDTO.class)).collect(Collectors.toList());
            LoginResponse loginResponse = new LoginResponse(token, userDTO, studentDTOList);
            return new ApiResponse<>(true, "Login successful", loginResponse);
        } catch (BadCredentialsException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        }
    }

    public void activeAccount(ActiveRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("Email not found!"));
        String password = passwordService.generateStrongRandomPassword();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        if (user.is_activated()) throw new RuntimeException("Account already activated");
        emailService.sendEmail(request.getEmail(), "Account activation password",
                "Hi,\n\nYour temporary password is: " + password
                        + "\nPlease change after login.\n\nThanks.");
        user.set_activated(true);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email has been registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getFullName() != null && !updatedUser.getFullName().isBlank()) {
            existingUser.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getRole() != null && !updatedUser.getRole().isBlank()) {
            existingUser.setRole(updatedUser.getRole());
        }
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().isBlank()) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null && !updatedUser.getAddress().isBlank()) {
            existingUser.setAddress(updatedUser.getAddress());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(id);
    }

    public List<User> searchUsers(String fullName, String role) {
        if (fullName != null && role != null) {
            return userRepository.findByFullNameContainingIgnoreCaseAndRoleOrderByFullNameAsc(fullName, role);
        } else if (fullName != null) {
            return userRepository.findByFullNameContainingIgnoreCase(fullName);
        } else if (role != null) {
            return userRepository.findByRoleOrderByFullNameAsc(role);
        } else {
            return userRepository.findAll();
        }
    }


    public HealthCheckProgram createHealthProgram(HealthCheckProgramDTO dto) {
        User admin = userRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        HealthCheckProgram p = new HealthCheckProgram();
        p.setHealthCheckName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setStatus(dto.getStatus());
        p.setNote(dto.getNote());
        p.setAdmin(admin);
        return healthRepo.save(p);
    }

    public List<HealthCheckProgram> getAllHealthPrograms() {
        return healthRepo.findAll();
    }

    public HealthCheckProgram updateHealthProgram(Long id, HealthCheckProgramDTO dto) {
        HealthCheckProgram p = healthRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        p.setHealthCheckName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        p.setStatus(dto.getStatus());
        p.setNote(dto.getNote());
        return healthRepo.save(p);
    }

    public void deleteHealthProgram(Long id) {
        healthRepo.deleteById(id);
    }

    // CRUD VaccineProgram

    public VaccineProgram createVaccineProgram(VaccineProgramDTO dto) {
        User admin = userRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        VaccineProgram p = new VaccineProgram();
        p.setVaccineName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setVaccineDate(dto.getVaccineDate());
        p.setNote(dto.getNote());
        p.setAdmin(admin);
        return vaccineRepo.save(p);
    }

    public List<VaccineProgram> getAllVaccinePrograms() {
        return vaccineRepo.findAll();
    }

    public VaccineProgram updateVaccineProgram(Long id, VaccineProgramDTO dto) {
        VaccineProgram p = vaccineRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        p.setVaccineName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setVaccineDate(dto.getVaccineDate());
        p.setNote(dto.getNote());
        return vaccineRepo.save(p);
    }

    public void deleteVaccineProgram(Long id) {
        vaccineRepo.deleteById(id);
    }
}
