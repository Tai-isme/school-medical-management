package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.entities.MedicalEvent;
import com.swp391.school_medical_management.modules.users.entities.Student;
import com.swp391.school_medical_management.modules.users.entities.User;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nurse")
@PreAuthorize("hasRole('ROLE_NURSE')")
public class NurseController {
    @Autowired
    private NurseService nurseService;
    @Autowired
    private StudentRepository  studentRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/healthform")
    public ResponseEntity<?> createHealthForm(@RequestBody HealthCheckFormDTO dto) {
        return ResponseEntity.ok(nurseService.createHealthForm(dto));
    }

    @GetMapping("/healthform")
    public ResponseEntity<?> getAllHealthForms() {
        return ResponseEntity.ok(nurseService.getAllHealthForms());
    }

    @PostMapping("/vaccineform")
    public ResponseEntity<?> createVaccineForm(@RequestBody VaccineFormDTO dto) {
        return ResponseEntity.ok(nurseService.createVaccineForm(dto));
    }

    @GetMapping("/vaccineform")
    public ResponseEntity<?> getAllVaccineForms() {
        return ResponseEntity.ok(nurseService.getAllVaccineForms());
    }

    // Lọc các form đã commit
    @GetMapping("/healthform/committed")
    public ResponseEntity<?> getCommittedHealthForms() {
        return ResponseEntity.ok(nurseService.getCommittedHealthForms());
    }

    @GetMapping("/vaccineform/committed")
    public ResponseEntity<?> getCommittedVaccineForms() {
        return ResponseEntity.ok(nurseService.getCommittedVaccineForms());
    }

    @PutMapping("/healthform/{id}")
    public ResponseEntity<?> updateHealthForm(@PathVariable Long id, @RequestBody HealthCheckFormDTO dto) {
        return ResponseEntity.ok(nurseService.updateHealthForm(id, dto));
    }

    @PutMapping("/vaccineform/{id}")
    public ResponseEntity<?> updateVaccineForm(@PathVariable Long id, @RequestBody VaccineFormDTO dto) {
        return ResponseEntity.ok(nurseService.updateVaccineForm(id, dto));
    }

    // Xoá form
    @DeleteMapping("/healthform/{id}")
    public ResponseEntity<?> deleteHealthForm(@PathVariable Long id) {
        nurseService.deleteHealthForm(id);
        return ResponseEntity.ok("Deleted health check form with ID: " + id);
    }

    @DeleteMapping("/vaccineform/{id}")
    public ResponseEntity<?> deleteVaccineForm(@PathVariable Long id) {
        nurseService.deleteVaccineForm(id);
        return ResponseEntity.ok("Deleted vaccine form with ID: " + id);
    }

    @GetMapping("/healthform/search")
    public ResponseEntity<?> searchHealthForm(@RequestParam String keyword) {
        return ResponseEntity.ok(nurseService.searchHealthForms(keyword));
    }

    @GetMapping("/vaccine-form/search")
    public ResponseEntity<?> searchVaccineForm(@RequestParam String keyword) {
        return ResponseEntity.ok(nurseService.searchVaccineForms(keyword));
    }

    @PostMapping
    public MedicalEventDTO createEvent(@RequestBody MedicalEventDTO dto) {
        MedicalEvent event = buildEntity(dto);
        return mapToDTO(nurseService.createEvent(event));
    }

    @PutMapping("/{id}")
    public MedicalEventDTO updateEvent(@PathVariable Long id, @RequestBody MedicalEventDTO dto) {
        MedicalEvent event = buildEntity(dto);
        event.setEventId(id);
        return mapToDTO(nurseService.updateEvent(event));
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        nurseService.deleteEvent(id);
    }

    @GetMapping
    public List<MedicalEventDTO> getAllEvents() {
        return nurseService.getAllEvents().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MedicalEvent buildEntity(MedicalEventDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + dto.getStudentId()));

        User nurse = userRepository.findById(dto.getNurseId())
                .orElseThrow(() -> new RuntimeException("Nurse not found with id: " + dto.getNurseId()));

        return MedicalEvent.builder()
                .typeEvent(dto.getTypeEvent())
                .date(dto.getDate())
                .description(dto.getDescription())
                .student(student)
                .nurse(nurse)
                .build();
    }

    private MedicalEventDTO mapToDTO(MedicalEvent event) {
        return MedicalEventDTO.builder()
                .eventId(event.getEventId())
                .typeEvent(event.getTypeEvent())
                .date(event.getDate())
                .description(event.getDescription())
                .studentId(event.getStudent().getId())
                .studentName(event.getStudent().getStudentName())   // hoặc getStudentName() tùy entity
                .nurseId(event.getNurse().getId())
                .nurseName(event.getNurse().getFullName())
                .build();
    }

}
