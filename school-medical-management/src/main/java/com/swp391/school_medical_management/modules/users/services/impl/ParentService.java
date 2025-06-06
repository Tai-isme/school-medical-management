package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRecordsRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequestDetails;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.repositories.ClassRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private ClassRepository classRepository;

    public MedicalRecordDTO createMedicalRecord(Long parentId, MedicalRecordsRequest request) {
        StudentEntity student = studentRepository.findStudentById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (medicalRecordsRepository.findMedicalRecordByStudent_Id(request.getStudentId()).isPresent()) {
            throw new RuntimeException("Medical record already exist");
        }
        MedicalRecordEntity medicalRecord = new MedicalRecordEntity();
        medicalRecord.setStudent(student);
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setChronicDisease(request.getChronicDisease());
        medicalRecord.setTreatmentHistory(request.getTreatmentHistory());
        medicalRecord.setVision(request.getVision());
        medicalRecord.setHearing(request.getHearing());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setHeight(request.getHeight());
        medicalRecord.setNote(request.getNote());
        medicalRecordsRepository.save(medicalRecord);
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    public MedicalRecordDTO updateMedicalRecord(Long parentId, MedicalRecordsRequest request) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty() || !studentOpt.get().getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(request.getStudentId());
        if (recordOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        MedicalRecordEntity medicalRecord = recordOpt.get();
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setChronicDisease(request.getChronicDisease());
        medicalRecord.setTreatmentHistory(request.getTreatmentHistory());
        medicalRecord.setVision(request.getVision());
        medicalRecord.setHearing(request.getHearing());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setHeight(request.getHeight());
        medicalRecord.setNote(request.getNote());
        medicalRecordsRepository.save(medicalRecord);
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    public List<MedicalRecordDTO> getAllMedicalRecordByParentId(Long parentId) {
        List<MedicalRecordEntity> medicalRecordList = medicalRecordsRepository
                .findMedicalRecordByStudent_Parent_UserId(parentId);
        return medicalRecordList.stream().map(medicalRecord -> modelMapper.map(medicalRecord, MedicalRecordDTO.class))
                .collect(Collectors.toList());
    }

    public MedicalRecordDTO getMedicalRecordByStudentId(Long parentId, Long studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        if (!medicalRecord.getStudent().getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
    }

    public void deleteMedicalRecord(Long parentId, Long studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
                .findMedicalRecordByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        if (!medicalRecord.getStudent().getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        medicalRecordsRepository.delete(medicalRecord);
    }

    public MedicalRequestDTO createMedicalRequest(long parentId, MedicalRequest request) {
        StudentEntity student = studentRepository.findStudentById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        UserEntity parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found"));
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        boolean isMedicalRequestExist = medicalRequestRepository
                .existsByStudentAndStatus(student, "pending");
        if (isMedicalRequestExist) 
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical request already exists");
        if(request.getMedicalRequestDetails() == null || request.getMedicalRequestDetails().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }
        MedicalRequestEntity medicalRequestEntity = new MedicalRequestEntity();
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setStatus("pending");
        medicalRequestEntity.setCommit(false);
        medicalRequestEntity.setStudent(student);
        medicalRequestEntity.setParent(parent);
        medicalRequestEntity.setClassEntity(student.getClassEntity());
        medicalRequestEntity.setDate(LocalDateTime.now());

        medicalRequestEntity.setMedicalRequestDetails(new ArrayList<>());

        for (MedicalRequestDetails details : request.getMedicalRequestDetails()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setInstruction(details.getInstructions());
            medicalRequestDetailEntity.setQuantity(details.getQuantity());
            medicalRequestDetailEntity.setTime(details.getTime());

            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);

            medicalRequestEntity.getMedicalRequestDetails().add(medicalRequestDetailEntity);
        }
        
        medicalRequestRepository.save(medicalRequestEntity);
        return modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
    }

    public MedicalRequestDTO getMedicalRequestByRequestId(Long parentId, Integer requestId) {
        MedicalRequestEntity medicalRequest = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found"));
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest.getMedicalRequestDetails();
        if (!medicalRequest.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        medicalRequestDTO.setTeacherName(medicalRequest.getStudent().getClassEntity().getTeacherName());
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }

    public List<MedicalRequestDTO> getMedicalRequestByStudent(Long parentId, Long studentId) {
        StudentEntity student = studentRepository.findStudentById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!student.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findMedicalRequestEntityByStudent(student);
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
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

    public MedicalRequestDTO updateMedicalRequest(Long parentId, MedicalRequest request, Integer requestId) {
        MedicalRequestEntity medicalRequestEntity = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found"));
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if(medicalRequestEntity.getStatus().equals("approved") || medicalRequestEntity.getStatus().equals("rejected")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a request that has already been processed");
        }
        if(request.getMedicalRequestDetails() == null || request.getMedicalRequestDetails().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
    
        medicalRequestEntity.getMedicalRequestDetails().clear();

            for (MedicalRequestDetails details : request.getMedicalRequestDetails()) {
                MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
                medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
                medicalRequestDetailEntity.setInstruction(details.getInstructions());
                medicalRequestDetailEntity.setQuantity(details.getQuantity());
                medicalRequestDetailEntity.setTime(details.getTime());
                medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);
                medicalRequestEntity.getMedicalRequestDetails().add(medicalRequestDetailEntity);
            }

        medicalRequestRepository.save(medicalRequestEntity);
        return modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
    }

    public void deleteMedicalRequest(Long parentId, int requestId) {
        MedicalRequestEntity medicalRequestEntity = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found"));
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if(medicalRequestEntity.getStatus().equals("approved") || medicalRequestEntity.getStatus().equals("rejected")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete a request that has already been processed");
        }
        medicalRequestRepository.delete(medicalRequestEntity);
    }
}
