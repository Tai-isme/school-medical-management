package com.swp391.school_medical_management.modules.users.controllers;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.services.impl.NurseService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("api/nurse")
@PreAuthorize("hasRole('ROLE_NURSE')")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @GetMapping("/medical-request/pending")
    public ResponseEntity<List<MedicalRequestDTO>> getPendingMedicalRequest() {
        List<MedicalRequestDTO> medicalrequestDTOList = nurseService.getPendingMedicalRequest();
        return ResponseEntity.ok(medicalrequestDTOList);
    }

    @PutMapping("/medical-request/{requestId}/status")
    public ResponseEntity<MedicalRequestDTO> updateMedicalRequestStatus(
        @PathVariable int requestId,
        @RequestParam String status) {
    MedicalRequestDTO medicalRequestDTO = nurseService.updateMedicalRequestStatus(requestId, status);
    return ResponseEntity.ok(medicalRequestDTO);
    }
}
