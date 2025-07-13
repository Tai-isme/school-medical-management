package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.CommitHealthCheckFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.CommitVaccineFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.FeedbackRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRecordsRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequestDetailRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineHistoryRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.AllFormsByStudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineHistoryDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultDTO;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity;
import com.swp391.school_medical_management.modules.users.entities.FeedbackEntity.FeedbackStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity.HealthCheckFormStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalEventEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity.VaccineFormStatus;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.repositories.FeedbackRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineNameRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineResultRepository;

@Service
public class ParentService {

    private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

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
    private HealthCheckFormRepository healthCHeckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private VaccineResultRepository vaccineResultRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private VaccineNameRepository vaccineNameRepository;

    public MedicalRecordDTO createMedicalRecord(Long parentId, MedicalRecordsRequest request) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        StudentEntity student = studentOpt.get();
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (medicalRecordsRepository.findMedicalRecordByStudent_Id(request.getStudentId()).isPresent()) {
            throw new RuntimeException("Medical record already exists");
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

        List<VaccineHistoryEntity> vaccineHistories = new ArrayList<>();
        for (VaccineHistoryRequest vaccineHistoryRequest : request.getVaccineHistories()) {
            VaccineHistoryEntity vaccineHistory = new VaccineHistoryEntity();

            Long vaccineNameId = vaccineHistoryRequest.getVaccineNameId();
            VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(vaccineNameId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name not found"));

            vaccineHistory.setVaccineNameEntity(vaccineNameEntity);
            vaccineHistory.setNote(vaccineHistoryRequest.getNote());
            vaccineHistory.setMedicalRecord(medicalRecord);

            vaccineHistories.add(vaccineHistory);
        }

        medicalRecord.setVaccineHistories(vaccineHistories);
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
        if (recordOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }

        MedicalRecordEntity medicalRecord = recordOpt.get();
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setChronicDisease(request.getChronicDisease());
        medicalRecord.setTreatmentHistory(request.getTreatmentHistory());
        medicalRecord.setVision(request.getVision());
        medicalRecord.setHearing(request.getHearing());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setHeight(request.getHeight());
        medicalRecord.setNote(request.getNote());
        medicalRecord.setLastUpdate(LocalDateTime.now());

        medicalRecord.getVaccineHistories().clear();

        for (VaccineHistoryRequest vaccineHistoryRequest : request.getVaccineHistories()) {
            VaccineHistoryEntity vaccineHistoryEntity = new VaccineHistoryEntity();

            Long vaccineNameId = vaccineHistoryRequest.getVaccineNameId();
            VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(vaccineNameId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name not found"));

            vaccineHistoryEntity.setVaccineNameEntity(vaccineNameEntity);
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
        if (optMedicalRecord.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found");
        }

        MedicalRecordEntity medicalRecord = optMedicalRecord.get();

        if (!medicalRecord.getStudent().getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        List<VaccineHistoryDTO> vaccineHistoryDTOList = medicalRecord.getVaccineHistories().stream()
                .map(vaccineHistory -> {
                    VaccineHistoryDTO dto = new VaccineHistoryDTO();
                    dto.setId(vaccineHistory.getId() != null ? vaccineHistory.getId().longValue() : null);
                    dto.setNote(vaccineHistory.getNote());
                    dto.setVaccineName(modelMapper.map(
                            vaccineHistory.getVaccineNameEntity(), VaccineNameDTO.class));
                    return dto;
                }).collect(Collectors.toList());

        MedicalRecordDTO medicalRecordDTO = modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        medicalRecordDTO.setVaccineHistories(vaccineHistoryDTOList);
        return medicalRecordDTO;
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
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        UserEntity parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found"));
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        boolean isMedicalRequestExist = medicalRequestRepository
                .existsByStudentAndStatus(student, MedicalRequestStatus.PROCESSING);
        if (isMedicalRequestExist)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical request already exists");

        if (request.getMedicalRequestDetailRequests() == null || request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }

        if (request.getDate() == null || request.getDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request date must be today or in the future");
        }

        MedicalRequestEntity medicalRequestEntity = new MedicalRequestEntity();
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setStatus(MedicalRequestStatus.PROCESSING);
        medicalRequestEntity.setStudent(student);
        medicalRequestEntity.setParent(parent);
        medicalRequestEntity.setDate(request.getDate());

        medicalRequestEntity.setMedicalRequestDetailEntities(new ArrayList<>());

        for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setDosage(details.getDosage());
            medicalRequestDetailEntity.setTime(details.getTime());

            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);

            medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        }

        medicalRequestRepository.save(medicalRequestEntity);
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestEntity
                .getMedicalRequestDetailEntities()
                .stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                        MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }

    public List<MedicalRequestDTO> getMedicalRequestByParent(Long parentId) {
        Optional<UserEntity> parentOpt = userRepository.findById(parentId);
        if (parentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found");
        UserEntity parent = parentOpt.get();
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findByParent(parent);
        if (medicalRequestEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found medical request");

        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);

            // Map chi tiết đơn thuốc
            List<MedicalRequestDetailDTO> detailDTOs = medicalRequestEntity.getMedicalRequestDetailEntities()
                    .stream()
                    .map(detail -> modelMapper.map(detail, MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            medicalRequestDTO.setMedicalRequestDetailDTO(detailDTOs);

            // Map student nếu cần
            // if (medicalRequestEntity.getStudent() != null) {
            // StudentDTO studentDTO = modelMapper.map(medicalRequestEntity.getStudent(),
            // StudentDTO.class);
            // medicalRequestDTO.setStudentDTO(studentDTO);
            // }

            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public MedicalRequestDTO getMedicalRequestByRequestId(Long parentId, Integer requestId) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest
                .getMedicalRequestDetailEntities();
        if (!medicalRequest.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                        MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        if (medicalRequest.getStudent() != null) {
            StudentDTO studentDTO = modelMapper.map(medicalRequest.getStudent(), StudentDTO.class);
            medicalRequestDTO.setStudentDTO(studentDTO);
        }
        return medicalRequestDTO;
    }

    public List<MedicalRequestDTO> getMedicalRequestByStudent(Long parentId, Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        if (!student.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository
                .findMedicalRequestEntityByStudent(student);
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
                    .getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
                    .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                            MedicalRequestDetailDTO.class))
                    .collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }

    public MedicalRequestDTO updateMedicalRequest(Long parentId, MedicalRequest request, Integer requestId) {
        Optional<MedicalRequestEntity> medicalRequestEntityOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestEntityOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if (!medicalRequestEntity.getStatus().equals(MedicalRequestStatus.PROCESSING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update a request that has already been processed");
        }
        if (request.getMedicalRequestDetailRequests() == null || request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setDate(request.getDate());

        medicalRequestEntity.getMedicalRequestDetailEntities().clear();

        for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setDosage(details.getDosage());
            medicalRequestDetailEntity.setTime(details.getTime());
            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);
            medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        }

        medicalRequestRepository.save(medicalRequestEntity);
        return modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
    }

    public void deleteMedicalRequest(Long parentId, int requestId) {
        Optional<MedicalRequestEntity> medicalRequestEntityOpt = medicalRequestRepository
                .findMedicalRequestEntityByRequestId(requestId);
        if (medicalRequestEntityOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        if (!medicalRequestEntity.getStatus().equals(MedicalRequestStatus.PROCESSING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete a request that has already been processed");
        }
        medicalRequestRepository.delete(medicalRequestEntity);
    }

    public List<HealthCheckFormDTO> getAllHealthCheckForm(Long parentId, Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity studentEntity = studentOpt.get();
        List<HealthCheckFormEntity> healthCheckFormEntities = healthCHeckFormRepository
                .findAllByStudentAndStatus(studentEntity, HealthCheckFormStatus.SENT);

        List<HealthCheckFormDTO> healthCheckFormDTOs = healthCheckFormEntities.stream()
                .filter(form -> form.getParent().getUserId().equals(parentId))
                .map(form -> modelMapper.map(form, HealthCheckFormDTO.class))
                .collect(Collectors.toList());
        return healthCheckFormDTOs;
    }

    public List<VaccineFormDTO> getAllVaccineForm(Long parentId, Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity studentEntity = studentOpt.get();
        List<VaccineFormEntity> vaccineFormEntities = vaccineFormRepository.findAllByStudentAndStatus(studentEntity,
                VaccineFormStatus.SENT);

        List<VaccineFormDTO> vaccineFormDTOs = vaccineFormEntities.stream()
                .filter(form -> form.getStudent().getParent().getUserId().equals(parentId))
                .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class))
                .collect(Collectors.toList());
        return vaccineFormDTOs;
    }

    public HealthCheckFormDTO getHealthCheckForm(Long parentId, Long healthCheckFormId) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCHeckFormRepository
                .findByIdAndStatus(healthCheckFormId, HealthCheckFormStatus.SENT);
        if (healthCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();

        if (healthCheckFormEntity.getParent() == null
                || !healthCheckFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
        return healthCheckFormDTO;
    }

    public VaccineFormDTO getVaccineForm(Long parentId, Long vaccineFormId) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository
                .findByIdAndStatus(vaccineFormId, VaccineFormStatus.SENT);
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if (vaccineFormEntity.getParent() == null
                || !vaccineFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        VaccineFormDTO vaccineFormDTO = modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
        return vaccineFormDTO;
    }

    public List<HealthCheckResultDTO> getHealthCheckResults(Long studentId) {
        List<HealthCheckResultEntity> healthCheckResultList = healthCheckResultRepository
                .findByHealthCheckFormEntity_Student_Id(studentId);
        if (healthCheckResultList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");

        List<HealthCheckResultDTO> healthCheckResultDTOList = healthCheckResultList.stream().map(entity -> {
            HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);
            HealthCheckFormEntity form = entity.getHealthCheckFormEntity();
            dto.setHealthCheckFormId(form.getId());
            dto.setStudentId(form.getStudent().getId());
            return dto;
        }).collect(Collectors.toList());

        return healthCheckResultDTOList;
    }

    public List<VaccineResultDTO> getVaccineResults(Long studentId) {
        List<VaccineResultEntity> vaccineResultList = vaccineResultRepository
                .findByVaccineFormEntity_Student_Id(studentId);
        if (vaccineResultList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");

        List<VaccineResultDTO> vaccineResultDTOList = vaccineResultList.stream().map(entity -> {
            VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);
            VaccineFormEntity form = entity.getVaccineFormEntity();
            dto.setVaccineFormId(form.getId());
            dto.setStudentId(form.getStudent().getId());
            return dto;
        }).collect(Collectors.toList());
        return vaccineResultDTOList;
    }

    public void commitHealthCheckForm(Long parentId, Long healthCheckFormId, CommitHealthCheckFormRequest request) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCHeckFormRepository
                .findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        if (healthCheckFormEntity.getParent() == null
                || !healthCheckFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not allowed to commit this health check form");

        if (healthCheckFormEntity.getCommit() != null && healthCheckFormEntity.getCommit())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed");

        healthCheckFormEntity.setCommit(request.isCommit());
        healthCheckFormEntity.setNotes(request.getNote());
        healthCHeckFormRepository.save(healthCheckFormEntity);
    }

    public void commitVaccineForm(Long parentId, Long vaccineFormId, CommitVaccineFormRequest request) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository
                .findById(vaccineFormId);
        if (vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if (vaccineFormEntity.getParent() == null || !vaccineFormEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You not allowed to commit this vaccine form");

        if (vaccineFormEntity.getCommit() != null && vaccineFormEntity.getCommit())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed");

        vaccineFormEntity.setCommit(request.isCommit());
        vaccineFormEntity.setNote(request.getNote());
        vaccineFormRepository.save(vaccineFormEntity);
    }

    public void submitFeedback(FeedbackRequest request) {
        UserEntity parent = userRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO PARENT"));

        FeedbackEntity feedback = FeedbackEntity.builder()
                .satisfaction(FeedbackEntity.Satisfaction.valueOf(request.getSatisfaction().toUpperCase()))
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .status(FeedbackStatus.NOT_REPLIED)
                .parent(parent)
                .build();
        if (request.getVaccineResultId() != null) {
            VaccineResultEntity vr = vaccineResultRepository.findById(request.getVaccineResultId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO VACCINE RESULT"));
            feedback.setVaccineResult(vr);
        }
        if (request.getHealthResultId() != null) {
            HealthCheckResultEntity hr = healthCheckResultRepository.findById(request.getHealthResultId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "DON'T FIND TO HEALTH CHECK RESULT"));
            feedback.setHealthResult(hr);
        }
        feedbackRepository.save(feedback);
    }

    public List<FeedbackDTO> getFeedbacksByParent(Long parentId) {
        UserEntity parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO PARENT"));
        List<FeedbackEntity> feedbackList = feedbackRepository.findByParent(parent);
        if (feedbackList.isEmpty()) {
            return Collections.emptyList();
        }

        List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
                .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());

        return feedbackDTOList;
    }

    public List<MedicalEventDTO> getMedicalEventsByStudent(Long parentId, Long studentId) {

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        StudentEntity studentEntity = studentOpt.get();

        if (!studentEntity.getParent().getUserId().equals(parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");

        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any medical event");

        List<MedicalEventDTO> medicalEventDTOList = medicalEventEntitieList
                .stream()
                .map(medicalEventEntitie -> modelMapper
                        .map(medicalEventEntitie, MedicalEventDTO.class))
                .collect(Collectors
                        .toList());
        return medicalEventDTOList;
    }

    public AllFormsByStudentDTO getAllFormByStudent(Long parentId, Long studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        if (!student.getParent().getUserId().equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        AllFormsByStudentDTO allFormsByStudentDTO = new AllFormsByStudentDTO();
        List<HealthCheckFormDTO> healthCheckForms = getAllHealthCheckForm(parentId, studentId);
        if (!healthCheckForms.isEmpty()) {
            allFormsByStudentDTO.setHealthCheckForms(Collections.<HealthCheckFormDTO>emptyList());
        } else {
            allFormsByStudentDTO.setHealthCheckForms(Collections.emptyList());
        }

        List<VaccineFormDTO> vaccineForms = getAllVaccineForm(parentId, studentId);
        if (!vaccineForms.isEmpty()) {
            allFormsByStudentDTO.setVaccineForms(vaccineForms);
        } else {
            allFormsByStudentDTO.setVaccineForms(Collections.emptyList());
        }
        return allFormsByStudentDTO;
    }

    public HealthCheckResultDTO getHealthCheckResultByFormId(Long formId) {
        Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository
                .findByHealthCheckFormEntity_Id(formId);

        if (healthCheckResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Health check result not found for formId: " + formId);

        HealthCheckResultEntity entity = healthCheckResultOpt.get();
        HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

        HealthCheckFormEntity form = entity.getHealthCheckFormEntity();
        dto.setHealthCheckFormId(form.getId());
        dto.setStudentId(form.getStudent().getId());

        return dto;
    }

    public VaccineResultDTO getVaccineResultByFormId(Long formId) {
        Optional<VaccineResultEntity> vaccineResultOpt = vaccineResultRepository.findByVaccineFormEntity_Id(formId);

        if (vaccineResultOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found for formId: " + formId);

        VaccineResultEntity entity = vaccineResultOpt.get();
        VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

        VaccineFormEntity form = entity.getVaccineFormEntity();
        dto.setVaccineFormId(form.getId());
        dto.setStudentId(form.getStudent().getId());

        return dto;
    }

    public List<VaccineNameDTO> getAllVaccineNames() {
        return vaccineNameRepository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, VaccineNameDTO.class))
                .collect(Collectors.toList());
    }

}
