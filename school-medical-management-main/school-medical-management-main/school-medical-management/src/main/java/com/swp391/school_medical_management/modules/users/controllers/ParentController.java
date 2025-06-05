package com.swp391.school_medical_management.modules.users.controllers;



import com.swp391.school_medical_management.helpers.ApiResponse;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.services.impl.ParentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parent")
@PreAuthorize("hasRole('ROLE_PARENT')")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @GetMapping("/data")
    public ResponseEntity<?> getUserInfo() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", parentService.getUserInfo(id)));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getStudentById(id));
    }

    @GetMapping("/medicalrecord/{studentId}")
    public ResponseEntity<?> getMedicalRecord(@PathVariable Long studentId) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(parentService.getMedicalRecord(studentId, parentId));
    }

    @PostMapping("/medicalrecord")
    public ResponseEntity<?> createMedicalRecord(@RequestBody MedicalRecordDTO dto) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.createMedicalRecord(dto, parentId);
        return ResponseEntity.ok("Tạo hồ sơ sức khỏe thành công.");
    }

    @PutMapping("/medicalrecord/{id}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecordDTO dto) {
        String parentId = SecurityContextHolder.getContext().getAuthentication().getName();
        parentService.updateMedicalRecord(id, dto, parentId);
        return ResponseEntity.ok("Cập nhật hồ sơ thành công.");
    }
}

