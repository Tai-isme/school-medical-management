package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Validated
@RestController
@RequestMapping("api/nurse")
@PreAuthorize("hasRole('ROLE_NURSE')")
public class NurseController {
    @Autowired
    private NurseService nurseService;

    @GetMapping("/medical-request/processing")
    public ResponseEntity<List<MedicalRequestDTO>> getPendingMedicalRequest() {
        List<MedicalRequestDTO> medicalrequestDTOList = nurseService.getPendingMedicalRequest();
        return ResponseEntity.ok(medicalrequestDTOList);
    }

    @PutMapping("/medical-request/{requestId}/status")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequestStatus(@PathVariable int requestId,@Valid @RequestParam String status) {
    MedicalRequestDTO medicalRequestDTO = nurseService.updateMedicalRequestStatus(requestId, status);
    return ResponseEntity.ok(medicalRequestDTO);
    }

    @PostMapping("/health-check-forms")
    public ResponseEntity<List<HealthCheckFormDTO>> createHealthCheckForm(@RequestBody HealthCheckFormCreateRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healCheckFormDTOList = nurseService.createHealthCheckForm(Long.parseLong(nurseId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(healCheckFormDTOList);
    }

    @PutMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<Map<String, Object>> updateHealthCheckForm(@RequestBody HealthCheckFormUpdateRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> healCheckFormDTO = nurseService.updateHealthCheckForm(Long.parseLong(nurseId), request);
        return ResponseEntity.ok(healCheckFormDTO);
    }

    @GetMapping("/health-check-forms")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllHealthCheckForms() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = nurseService.getAllHealthCheckForms(Long.parseLong(nurseId));
        return ResponseEntity.ok(healthCheckFormDTOList);
    }
    
    @GetMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<HealthCheckFormDTO> getHealthCheckFormById(@PathVariable Long healthCheckFormId) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        HealthCheckFormDTO healthCheckFormDTO = nurseService.getHealthCheckFormById(Long.parseLong(nurseId), healthCheckFormId);
        return ResponseEntity.ok(healthCheckFormDTO);
    }

    @GetMapping("/health-check-forms/commited")
    public ResponseEntity<List<HealthCheckFormDTO>> getAllCommitedTrueHealthCheckForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<HealthCheckFormDTO> healthCheckFormDTOList = nurseService.getAllCommitedTrueHealthCheckForm(Long.parseLong(nurseId));
        return ResponseEntity.ok(healthCheckFormDTOList);
    }
    
    @DeleteMapping("/health-check-forms/{healthCheckFormId}")
    public ResponseEntity<Void> deleteHealthCheckForm(@PathVariable Long healthCheckFormId) {
        nurseService.deleteHealthCheckForm(healthCheckFormId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/vaccine-forms")
    public ResponseEntity<List<VaccineFormDTO>> createVaccineForm(@RequestBody VaccineFormCreateRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = nurseService.createVaccineForm(Long.parseLong(nurseId), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccineFormDTOList);
    }

    @PutMapping("/vaccine-forms/{vaccineFormId}")
    public ResponseEntity<Map<String, Object>> updateVaccineForm(@RequestBody VaccineFormUpdateRequest request) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> vaccineFormDTO = nurseService.updateVaccineForm(Long.parseLong(nurseId), request);
        return ResponseEntity.ok(vaccineFormDTO);
    }
    
    @GetMapping("/vaccine-forms")
    public ResponseEntity<List<VaccineFormDTO>> getAllVaccinForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = nurseService.getAllVaccinForm(Long.parseLong(nurseId));
        return ResponseEntity.ok(vaccineFormDTOList);
    }
    
    @GetMapping("/vaccine-forms/{vaccineFormId}")
    public ResponseEntity<VaccineFormDTO> getVaccineFormById(@PathVariable Long vaccineFormId) {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        VaccineFormDTO vaccineFormDTO = nurseService.getVaccinFormById(Long.parseLong(nurseId), vaccineFormId);
        return ResponseEntity.ok(vaccineFormDTO);
    }
 
    @GetMapping("/vaccine-forms/commited")
    public ResponseEntity<List<VaccineFormDTO>> getAllCommitedTrueVaccineForm() {
        String nurseId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VaccineFormDTO> vaccineFormDTOList = nurseService.getAllCommitedTrueVaccineForm(Long.parseLong(nurseId));
        return ResponseEntity.ok(vaccineFormDTOList);
    }

    @DeleteMapping("/vaccine-forms/{vaccineFormId}")
    public ResponseEntity<Void> deleteVaccineForm(@PathVariable Long vaccineFormId) {
        nurseService.deleteVaccineForm(vaccineFormId);
        return ResponseEntity.noContent().build();
    }
    
}
