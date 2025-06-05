package com.swp391.school_medical_management.modules.users.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;

@Service
public class NurseService {
    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        String status = "PENDING";
        List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository.findMedicalRequestEntitiesByStatusIgnoreCase(status);
        if(pendingMedicalRequestList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetails();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setTeacherName(medicalRequestEntity.getStudent().getClassEntity().getTeacherName());
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
       return medicalRequestDTOList;
    }

    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, String status) {
        MedicalRequestEntity medicalRequest = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found"));
            if(status == null || (!status.toUpperCase().equals("APPROVED") && !status.toUpperCase().equals("REJECTED"))) 
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        medicalRequest.setStatus(status.toUpperCase());
        medicalRequestRepository.save(medicalRequest);
        return modelMapper.map(medicalRequest, MedicalRequestDTO.class);
    }

    
}
