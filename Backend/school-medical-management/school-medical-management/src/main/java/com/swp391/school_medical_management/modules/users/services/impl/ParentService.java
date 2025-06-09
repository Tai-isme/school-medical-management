package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.CommitHealthCheckFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.CommitVaccineFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRecordsRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequestDetailRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineHistoryRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineHistoryDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

    @Autowired private StudentRepository studentRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired private ModelMapper modelMapper;

    @Autowired private MedicalRequestRepository medicalRequestRepository;

    @Autowired private HealthCheckFormRepository healthCHeckFormRepository;

    @Autowired private VaccineFormRepository vaccineFormRepository;


    public MedicalRecordDTO createMedicalRecord(Long parentId, MedicalRecordsRequest request) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if(studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
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
        medicalRecord.setLastUpdate(LocalDateTime.now());

        medicalRecord.setVaccineHistories(new ArrayList<>());
        
        for (VaccineHistoryRequest vaccineHistoryRequest : request.getVaccineHistories()) {
                VaccineHistoryEntity vaccineHistory = new VaccineHistoryEntity();
                vaccineHistory.setVaccineName(vaccineHistoryRequest.getVaccineName());
                vaccineHistory.setNote(vaccineHistoryRequest.getNote());
                vaccineHistory.setMedicalRecord(medicalRecord);
                medicalRecord.getVaccineHistories().add(vaccineHistory);
        }
        medicalRecordsRepository.save(medicalRecord);
        MedicalRecordDTO medicalRecordDTO = modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        List<VaccineHistoryDTO> vaccineHistoryDTOList = medicalRecord.getVaccineHistories()
                .stream()
                .map(vaccineHistory -> modelMapper.map(vaccineHistory, VaccineHistoryDTO.class))
                .collect(Collectors.toList());
        medicalRecordDTO.setVaccineHistories(vaccineHistoryDTOList);

        return medicalRecordDTO;
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

        medicalRecord.getVaccineHistories().clear();

            for (VaccineHistoryRequest vaccineHistoryRequest : request.getVaccineHistories()) {
                VaccineHistoryEntity vaccineHistoryEntity = new VaccineHistoryEntity();
                vaccineHistoryEntity.setVaccineName(vaccineHistoryRequest.getVaccineName());
                vaccineHistoryEntity.setNote(vaccineHistoryRequest.getNote());
                vaccineHistoryEntity.setMedicalRecord(medicalRecord);                
                medicalRecord.getVaccineHistories().add(vaccineHistoryEntity);
            }

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
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if(studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        UserEntity parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found"));
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        
        boolean isMedicalRequestExist = medicalRequestRepository
                .existsByStudentAndStatus(student, "PROCESSING");
        if (isMedicalRequestExist) 
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical request already exists");

        // boolean isMedicalRequestExist = medicalRequestRepository
        // .existsByStudentAndStatus(student, "PROCESSING");

        // if (isMedicalRequestExist && !"test".equals(environment)) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical request already exists");
        // }

        if(request.getMedicalRequestDetailRequests() == null || request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }

        if (request.getDate() == null || request.getDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request date must be today or in the future");
        }

        MedicalRequestEntity medicalRequestEntity = new MedicalRequestEntity();
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setStatus("PROCESSING");
        medicalRequestEntity.setStudent(student);
        medicalRequestEntity.setParent(parent);
        medicalRequestEntity.setDate(request.getDate());

        medicalRequestEntity.setMedicalRequestDetailEntities(new ArrayList<>());

        for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setInstruction(details.getInstructions());
            medicalRequestDetailEntity.setDosage(details.getDosage());
            medicalRequestDetailEntity.setTime(details.getTime());

            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);

            medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        }
        
        medicalRequestRepository.save(medicalRequestEntity);
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestEntity.getMedicalRequestDetailEntities()
                .stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }

    public MedicalRequestDTO getMedicalRequestByRequestId(Long parentId, Integer requestId) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest.getMedicalRequestDetailEntities();
        if (!medicalRequest.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");   
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }

    public List<MedicalRequestDTO> getMedicalRequestByStudent(Long parentId, Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        if (!student.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findMedicalRequestEntityByStudent(student);
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public MedicalRequestDTO updateMedicalRequest(Long parentId, MedicalRequest request, Integer requestId) {
        Optional<MedicalRequestEntity> medicalRequestEntityOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if(medicalRequestEntityOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if(!medicalRequestEntity.getStatus().equals("PROCESSING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a request that has already been processed");
        }
        if(request.getMedicalRequestDetailRequests() == null || request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setDate(request.getDate());
    
        medicalRequestEntity.getMedicalRequestDetailEntities().clear();

            for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
                MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
                medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
                medicalRequestDetailEntity.setInstruction(details.getInstructions());
                medicalRequestDetailEntity.setDosage(details.getDosage());
                medicalRequestDetailEntity.setTime(details.getTime());
                medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);
                medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
            }

        medicalRequestRepository.save(medicalRequestEntity);
        return modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
    }

    public void deleteMedicalRequest(Long parentId, int requestId) {
        Optional<MedicalRequestEntity> medicalRequestEntityOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if(medicalRequestEntityOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if(!medicalRequestEntity.getStatus().equals("PROCESSING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete a request that has already been processed");
        }
        medicalRequestRepository.delete(medicalRequestEntity);
    }

    public void commitHealthCheckForm(Long parentId, Long healCheckFormId, CommitHealthCheckFormRequest request) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCHeckFormRepository.findHealCheckFormEntityById(healCheckFormId);
        if(healthCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        if(healthCheckFormEntity.getParent() == null || !healthCheckFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not allowed to commit this health check form");

        if(healthCheckFormEntity.getCommit() != null && healthCheckFormEntity.getCommit()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed");

        healthCheckFormEntity.setCommit(request.isCommit());
        healthCheckFormEntity.setNotes(request.getNote());
        healthCHeckFormRepository.save(healthCheckFormEntity);
    }

    public void commitVaccineForm(Long parentId, Long vaccineFormId, CommitVaccineFormRequest request) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if(vaccineFormEntity.getParent() == null || !vaccineFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not allowed to commit this vaccine form");

        if(vaccineFormEntity.getCommit() != null && vaccineFormEntity.getCommit()) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed");

        vaccineFormEntity.setCommit(request.isCommit());
        vaccineFormEntity.setNote(request.getNote());
        vaccineFormRepository.save(vaccineFormEntity);
    }
}
