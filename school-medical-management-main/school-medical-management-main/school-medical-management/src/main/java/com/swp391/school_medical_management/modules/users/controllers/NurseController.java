package com.swp391.school_medical_management.modules.users.controllers;

import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nurse")
@PreAuthorize("hasRole('ROLE_NURSE')")
public class NurseController {
    @Autowired
    private NurseService nurseService;

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

}
