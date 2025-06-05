package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getRole(),
                        user.getPhone(),
                        user.getAddress()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        UserDTO dto = new UserDTO(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getPhone(), user.getAddress());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            UserDTO dto = new UserDTO(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getRole(), savedUser.getPhone(), savedUser.getAddress());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updated = userService.updateUser(id, user);
            UserDTO dto = new UserDTO(updated.getId(), updated.getEmail(), updated.getFullName(), updated.getRole(), updated.getPhone(), updated.getAddress());
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Delete successfully!");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody UserDTO searchRequest) {
        String name = searchRequest.getFullName();
        String role = searchRequest.getRole();

        List<UserDTO> results = userService.searchUsers(name, role).stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }


    @PostMapping("/health")
    public ResponseEntity<?> createHealth(@RequestBody HealthCheckProgramDTO dto) {
        return ResponseEntity.ok(userService.createHealthProgram(dto));
    }

    @GetMapping("/health")
    public ResponseEntity<?> getAllHealth() {
        return ResponseEntity.ok(userService.getAllHealthPrograms());
    }

    @PutMapping("/health/{id}")
    public ResponseEntity<?> updateHealth(@PathVariable Long id, @RequestBody HealthCheckProgramDTO dto) {
        return ResponseEntity.ok(userService.updateHealthProgram(id, dto));
    }

    @DeleteMapping("/health/{id}")
    public ResponseEntity<?> deleteHealth(@PathVariable Long id) {
        userService.deleteHealthProgram(id);
        return ResponseEntity.ok("Deleted health program with ID: " + id);
    }


    @PostMapping("/vaccine")
    public ResponseEntity<?> createVaccine(@RequestBody VaccineProgramDTO dto) {
        return ResponseEntity.ok(userService.createVaccineProgram(dto));
    }

    @GetMapping("/vaccine")
    public ResponseEntity<?> getAllVaccines() {
        return ResponseEntity.ok(userService.getAllVaccinePrograms());
    }

    @PutMapping("/vaccine/{id}")
    public ResponseEntity<?> updateVaccine(@PathVariable Long id, @RequestBody VaccineProgramDTO dto) {
        return ResponseEntity.ok(userService.updateVaccineProgram(id, dto));
    }

    @DeleteMapping("/vaccine/{id}")
    public ResponseEntity<?> deleteVaccine(@PathVariable Long id) {
        userService.deleteVaccineProgram(id);
        return ResponseEntity.ok("Deleted vaccine program with ID: " + id);
    }
}
