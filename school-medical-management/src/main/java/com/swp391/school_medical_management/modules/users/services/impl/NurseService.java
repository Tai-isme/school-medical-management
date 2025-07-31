package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.entities.UserEntity.UserRole;
import com.swp391.school_medical_management.modules.users.repositories.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NurseService {

    private static final Logger logger = LoggerFactory.getLogger(NurseService.class);

    public static final String DEFAULT_VACCINE_HS_NOTE = "Chương trình vaccine tại trường!";

    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private VaccineProgramRepository vaccineProgramRepository;

    @Autowired
    private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired
    private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private VaccineResultRepository vaccineResultRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private VaccineHistoryRepository vaccineHistoryRepository;

    @Autowired
    private BlogRepository blogRepository;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        // List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository
        //         .findByStatus(MedicalRequestStatus.PROCESSING);
        // if (pendingMedicalRequestList.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No processing medical requests found");
        // }
        // List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        // for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
        //     List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
        //             .getMedicalRequestDetailEntities();
        //     List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
        //             .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
        //                     MedicalRequestDetailDTO.class))
        //             .collect(Collectors.toList());
        //     MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
        //     medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        //     medicalRequestDTOList.add(medicalRequestDTO);
        // }
        // return medicalRequestDTOList;
        return null;
    }

    public List<MedicalRequestDTO> getAllMedicalRequest() {
        List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findAll();
        if (medicalRequestEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
            StudentEntity studentEntity = medicalRequestEntity.getStudent();
            StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);
            List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity.getMedicalRequestDetailEntities();
            List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream().map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity, MedicalRequestDetailDTO.class)).collect(Collectors.toList());
            MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
            medicalRequestDTO.setStudentDTO(studentDTO);
            medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
            medicalRequestDTOList.add(medicalRequestDTO);
        }
        return medicalRequestDTOList;
    }


    public List<MedicalRequestDTO> getAllMedicalRequestByStatus(String statusStr) {
        // MedicalRequestStatus status;
        // try {
        //     status = MedicalRequestStatus.valueOf(statusStr.toUpperCase());
        // } catch (IllegalArgumentException e) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value");
        // }
        // List<MedicalRequestEntity> medicalRequestEntityList = medicalRequestRepository.findByStatus(status);
        // if (medicalRequestEntityList.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No medical requests found");
        // }
        // List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        // for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
        //     StudentEntity studentEntity = medicalRequestEntity.getStudent();
        //     StudentDTO studentDTO = modelMapper.map(studentEntity, StudentDTO.class);
        //     List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequestEntity
        //             .getMedicalRequestDetailEntities();
        //     List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestDetailEntityList.stream()
        //             .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
        //                     MedicalRequestDetailDTO.class))
        //             .collect(Collectors.toList());
        //     MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
        //     medicalRequestDTO.setStudentDTO(studentDTO);
        //     medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        //     medicalRequestDTOList.add(medicalRequestDTO);
        // }
        // return medicalRequestDTOList;
        return null;
    }

    public List<MedicalRequestDetailDTO> getMedicalRequestDetail(int requestId) {
        // Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
        //         .findMedicalRequestEntityByRequestId(requestId);
        // if (medicalRequestOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        // MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        // List<MedicalRequestDetailEntity> medicalRequestDetailEntityList = medicalRequest
        //         .getMedicalRequestDetailEntities();
        // if (medicalRequestDetailEntityList.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND,
        //             "No medical request details found for this request");
        // return medicalRequestDetailEntityList.stream()
        //         .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
        //                 MedicalRequestDetailDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public List<MedicalEventDTO> getMedicalEventsByStudent(int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");


        StudentEntity studentEntity = studentOpt.get();
        UserEntity parent = studentEntity.getParent();
        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found any medical event");
        List<MedicalEventDTO> medicalEventDTOList = medicalEventEntitieList.stream().map(event -> {
            MedicalEventDTO dto = modelMapper.map(event, MedicalEventDTO.class);


            if (event.getNurse() != null) {
                UserDTO nurseDTO = modelMapper.map(event.getNurse(), UserDTO.class);
                dto.setNurseDTO(nurseDTO);
            }
            if (event.getStudent() != null) {
                StudentDTO studentDTO = modelMapper.map(event.getStudent(), StudentDTO.class);
                dto.setStudentDTO(studentDTO);
            }
            if (parent != null) {
                UserDTO parentDTO = modelMapper.map(parent, UserDTO.class);
                dto.setParentDTO(parentDTO);
            }
            return dto;
        }).collect(Collectors.toList());
        return medicalEventDTOList;
    }


    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, UpdateMedicalRequestStatus request) {
        // Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
        //         .findMedicalRequestEntityByRequestId(requestId);
        // if (medicalRequestOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        // MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        // StudentEntity student = medicalRequest.getStudent();
        // logger.info(medicalRequest.getStatus().toString());
        // MedicalRequestStatus statusEnum;
        // try {
        //     statusEnum = MedicalRequestEntity.MedicalRequestStatus.valueOf(request.getStatus().name().toUpperCase());
        // } catch (IllegalArgumentException ex) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
        //         && (statusEnum == MedicalRequestStatus.PROCESSING)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update PROCESSING to PROCESSING");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
        //         && (statusEnum == MedicalRequestStatus.COMPLETED)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update PROCESSING to COMPLETED");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.PROCESSING
        //         && (statusEnum == MedicalRequestStatus.CANCELLED)) {

        //             logger.info(medicalRequest.getReason() + "   " + request.getReason_rejected());
        //     if (request.getReason_rejected().isBlank()) {
        //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                 "Reason is required when cancelling a PROCESSING request");
        //     }

        //     medicalRequest.setReason(request.getReason_rejected());
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
        //         && (statusEnum == MedicalRequestStatus.SUBMITTED)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update SUBMITTED to SUBMITTED");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
        //         && (statusEnum == MedicalRequestStatus.PROCESSING)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update SUBMITTED to PROCESSING");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.SUBMITTED
        //         && (statusEnum == MedicalRequestStatus.CANCELLED)) {

        //     if (request.getReason_rejected() == null || request.getReason_rejected().isEmpty()) {
        //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                 "Reason is required when cancelling a SUBMITTED request");
        //     }
        //     medicalRequest.setReason(request.getReason_rejected());
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.CANCELLED
        //         && (statusEnum == MedicalRequestStatus.CANCELLED)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update CANCELLED to CANCELLED");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.CANCELLED &&
        //         (statusEnum == MedicalRequestStatus.PROCESSING ||
        //                 statusEnum == MedicalRequestStatus.SUBMITTED ||
        //                 statusEnum == MedicalRequestStatus.CANCELLED ||
        //                 statusEnum == MedicalRequestStatus.COMPLETED)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update CANCELLED to CANCELLED, PROCESSING, SUBMITTED, or COMPLETED");
        // }

        // if (medicalRequest.getStatus() == MedicalRequestStatus.COMPLETED &&
        //         (statusEnum == MedicalRequestStatus.PROCESSING ||
        //                 statusEnum == MedicalRequestStatus.COMPLETED ||
        //                 statusEnum == MedicalRequestStatus.CANCELLED ||
        //                 statusEnum == MedicalRequestStatus.SUBMITTED)) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Cannot update COMPLETED to COMPLETED, PROCESSING or SUBMITTED");
        // }

        // medicalRequest.setStatus(statusEnum);
        // medicalRequestRepository.save(medicalRequest);
        // List<MedicalRequestDetailDTO> detailDTOs = medicalRequest.getMedicalRequestDetailEntities()
        //         .stream()
        //         .map(entity -> modelMapper.map(entity, MedicalRequestDetailDTO.class))
        //         .collect(Collectors.toList());
        // MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest, MedicalRequestDTO.class);
        // medicalRequestDTO.setMedicalRequestDetailDTO(detailDTOs);
        // medicalRequestDTO.setStudentDTO(modelMapper.map(student, StudentDTO.class));
        // return medicalRequestDTO;
        return null;
    }

    public HealthCheckFormDTO getHealthCheckFormById(int healthCheckFormId) {
        //     Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findById(healthCheckFormId);
        //     if (healthCheckFormOpt.isEmpty())
        //         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");

        //     HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();

        //     return modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
        return null;
    }

    public List<HealthCheckFormDTO> getHealthCheckFormsByProgram(int programId, Boolean committed) {
        // List<HealthCheckFormEntity> healthCheckForms;
        // if (Boolean.TRUE.equals(committed)) {
        //     healthCheckForms = healthCheckFormRepository.findByCommitTrueAndHealthCheckProgram_Id(programId);
        // } else {
        //     healthCheckForms = healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        // }

        // if (healthCheckForms.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check forms found");

        // return healthCheckForms.stream()
        //         .map(entity -> modelMapper.map(entity, HealthCheckFormDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public List<VaccineFormDTO> getVaccineFormsByProgram(int programId, Boolean committed) {
        // List<VaccineFormEntity> vaccineForms;

        // if (Boolean.TRUE.equals(committed)) {
        //     vaccineForms = vaccineFormRepository.findByCommitTrueAndVaccineProgram_VaccineId(programId);
        // } else {
        //     vaccineForms = vaccineFormRepository.findByVaccineProgram_VaccineId(programId);
        // }

        // if (vaccineForms.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine forms found");

        // return vaccineForms.stream()
        //         .map(form -> modelMapper.map(form, VaccineFormDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    // public Map<String, int> countDraftFormByProgram(int programId) {
    // int vaccineForm =
    // vaccineFormRepository.countByVaccineProgram_IdAndStatusAndCommitFalse(programId,
    // VaccineFormStatus.DRAFT);
    // int healthCheckForm =
    // healthCheckFormRepository.countByHealthCheckProgram_IdAndStatusAndCommitFalse(programId,
    // HealthCheckFormStatus.DRAFT);
    // return Map.of(
    // "vaccineForm", vaccineForm,
    // "healthCheckForm", healthCheckForm);
    // }

    public VaccineFormDTO getVaccinFormById(int vaccineFormId) {
        // Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);
        // if (vaccineFormOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        // return modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
        return null;
    }

    public List<VaccineFormDTO> getNotSentVaccineFormsByProgram(int programId) {
        // List<VaccineFormEntity> vaccineForms = vaccineFormRepository.findByStatusAndVaccineProgram_VaccineId(
        //         VaccineFormStatus.DRAFT,
        //         programId);
        // return vaccineForms.stream()
        //         .map(form -> modelMapper.map(form, VaccineFormDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public List<HealthCheckFormDTO> getNotSentHealthCheckFormsByProgram(int programId) {
        // List<HealthCheckFormEntity> healthCheckForms = healthCheckFormRepository
        //         .findByStatusAndHealthCheckProgram_Id(HealthCheckFormStatus.DRAFT, programId);

        // return healthCheckForms.stream()
        //         .map(form -> modelMapper.map(form, HealthCheckFormDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public OnGoingProgramDTO getOnGoingPrograms() {
        // List<VaccineProgramDTO> vaccinePrograms = vaccineProgramRepository.findByStatus(VaccineProgramStatus.ON_GOING)
        //         .stream()
        //         .map(vaccineprogram -> modelMapper.map(vaccineprogram, VaccineProgramDTO.class))
        //         .collect(Collectors.toList());

        // List<HealthCheckProgramDTO> healthCheckPrograms = healthCheckProgramRepository
        //         .findByStatus(HealthCheckProgramStatus.ON_GOING)
        //         .stream()
        //         .map(healthCheckProgram -> modelMapper.map(healthCheckProgram, HealthCheckProgramDTO.class))
        //         .collect(Collectors.toList());

        // return new OnGoingProgramDTO(vaccinePrograms, healthCheckPrograms);
        return null;
    }

    public MedicalEventDTO createMedicalEvent(int nurseId, MedicalEventRequest request) {
        // Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        // if (nurseOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");

        // UserEntity nurse = nurseOpt.get();

        // Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        // if (studentOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        // StudentEntity student = studentOpt.get();

        // Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository
        //         .findByStudentAndTypeEventAndDescription(student, request.getTypeEvent(), request.getDescription());
        // if (medicalEventOpt.isPresent())
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical event already exists");

        // MedicalEventEntity medicalEventEntity = new MedicalEventEntity();
        // medicalEventEntity.setTypeEvent(request.getTypeEvent());
        // medicalEventEntity.setDate(LocalDate.now());
        // medicalEventEntity.setDescription(request.getDescription());
        // medicalEventEntity.setStudent(student);
        // medicalEventEntity.setNurse(nurse);

        // medicalEventRepository.save(medicalEventEntity);

        // MedicalEventDTO dto = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
        // dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));

        // UserEntity parent = student.getParent();
        // if (parent != null) {
        //     UserDTO userDTO = UserDTO.builder()
        //             .id(parent.getUserId())
        //             .fullName(parent.getFullName())
        //             .email(parent.getEmail())
        //             .phone(parent.getPhone())
        //             .address(parent.getAddress())
        //             .role(parent.getRole())
        //             .build();

        //     dto.setParentDTO(userDTO);
        // }

        // return dto;
        return null;
    }

    public MedicalEventDTO updateMedicalEvent(int nurseId, int medicalEventId, MedicalEventRequest request) {
        // Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(medicalEventId);
        // if (medicalEventOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");

        // MedicalEventEntity medicalEventEntity = medicalEventOpt.get();

        // Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        // if (nurseOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");

        // UserEntity nurse = nurseOpt.get();

        // Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        // if (studentOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");

        // StudentEntity student = studentOpt.get();

        // medicalEventEntity.setTypeEvent(request.getTypeEvent());
        // medicalEventEntity.setDescription(request.getDescription());
        // medicalEventEntity.setStudent(student);
        // medicalEventEntity.setDate(LocalDate.now());
        // medicalEventEntity.setNurse(nurse);

        // medicalEventRepository.save(medicalEventEntity);

        // return modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
        return null;
    }

    public MedicalEventDTO getMedicalEvent(int medicalEventId) {
        // Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(medicalEventId);
        // if (medicalEventOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        // MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        // MedicalEventDTO medicalEventDTO = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);
        // if (medicalEventEntity.getNurse() != null) {
        //     UserDTO nurseDTO = modelMapper.map(medicalEventEntity.getNurse(), UserDTO.class);
        //     medicalEventDTO.setNurseDTO(nurseDTO);
        // }
        // if (medicalEventEntity.getStudent() != null) {
        //     StudentDTO studentDTO = modelMapper.map(medicalEventEntity.getStudent(), StudentDTO.class);
        //     medicalEventDTO.setStudentDTO(studentDTO);

        //     if (medicalEventEntity.getStudent().getParent() != null) {
        //         UserDTO parentDTO = modelMapper.map(medicalEventEntity.getStudent().getParent(), UserDTO.class);
        //         medicalEventDTO.setParentDTO(parentDTO);
        //     }
        // }
        // return medicalEventDTO;
        return null;
    }

    public List<MedicalEventDTO> getAllMedicalEvent() {
        // List<MedicalEventEntity> events = medicalEventRepository.findAll();

        // if (events.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        // }

        // List<MedicalEventDTO> medicalEventDTOList = new ArrayList<>();

        // for (MedicalEventEntity event : events) {
        //     MedicalEventDTO dto = new MedicalEventDTO();
        //     dto.setEventId(event.getEventId());
        //     dto.setTypeEvent(event.getTypeEvent());
        //     dto.setDate(event.getDate());
        //     dto.setDescription(event.getDescription());
        //     if (event.getNurse() != null) {
        //         UserDTO nurseDTO = modelMapper.map(event.getNurse(), UserDTO.class);
        //         dto.setNurseDTO(nurseDTO);
        //     }
        //     StudentEntity student = event.getStudent();
        //     if (student != null) {
        //         dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
        //         ClassEntity classEntity = student.getClassEntity();
        //         dto.setClassDTO(modelMapper.map(classEntity, ClassDTO.class));
        //         UserEntity parent = student.getParent();
        //         if (parent != null) {
        //             UserDTO userDTO = UserDTO.builder()
        //                     .id(parent.getUserId())
        //                     .fullName(parent.getFullName())
        //                     .email(parent.getEmail())
        //                     .phone(parent.getPhone())
        //                     .address(parent.getAddress())
        //                     .role(parent.getRole())
        //                     .build();

        //             dto.setParentDTO(userDTO);
        //         }
        //     }
        //     medicalEventDTOList.add(dto);
        // }
        // return medicalEventDTOList;
        return null;
    }

    public void deleteMedicalEvent(int meidcalEventId) {
        // Optional<MedicalEventEntity> medicalEventOpt = medicalEventRepository.findByEventId(meidcalEventId);
        // if (medicalEventOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical event not found");
        // MedicalEventEntity medicalEventEntity = medicalEventOpt.get();
        // medicalEventRepository.delete(medicalEventEntity);

    }

    public HealthCheckResultDTO createHealthCheckResult(HealthCheckResultRequest request) {

        // Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository
        //         .findById(request.getHealthCheckFormId());
        // if (healCheckFormOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        // HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        // if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Parent has not committed the health check form yet");
        // }

        // HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);

        // Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository
        //         .findByHealthCheckFormEntity(healthCheckFormEntity);
        // if (existingResultOpt.isPresent()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Health check result already exists for this student: "
        //                     + healthCheckFormEntity.getStudent().getId());
        // }

        // HealthCheckResultEntity healthCheckResultEntity = new HealthCheckResultEntity();
        // healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        // healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
        // healthCheckResultEntity.setNote(request.getNote());
        // healthCheckResultEntity.setHealthCheckFormEntity(healthCheckFormEntity);
        // healthCheckResultRepository.save(healthCheckResultEntity);

        // HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
        //         HealthCheckResultDTO.class);
        // healthCheckResultDTO.setHealthCheckFormDTO(healthCheckFormDTO);
        // healthCheckResultDTO.setStudentDTO(
        //         modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));

        // return healthCheckResultDTO;
        return null;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsForAllCommittedForms() {
        // List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findByCommitTrue();
        // List<HealthCheckResultDTO> resultList = new ArrayList<>();

        // for (HealthCheckFormEntity form : committedForms) {
        //     boolean exists = healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
        //     if (exists)
        //         continue;

        //     HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
        //     defaultRequest.setDiagnosis("GOOD");
        //     defaultRequest.setLevel("GOOD");
        //     defaultRequest.setNote("No problem");
        //     defaultRequest.setHealthCheckFormId(form.getId());

        //     try {
        //         HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
        //         resultList.add(resultDTO);
        //     } catch (ResponseStatusException ex) {
        //         System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
        //     }
        // }
        // return resultList;
        return null;
    }

    public List<HealthCheckResultDTO> createDefaultHealthCheckResultsByProgramId(int programId) {
        // List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findCommittedFormsByProgramId(programId);
        // List<HealthCheckResultDTO> resultList = new ArrayList<>();

        // for (HealthCheckFormEntity form : committedForms) {
        //     boolean exists = healthCheckResultRepository.findByHealthCheckFormEntity(form).isPresent();
        //     if (exists)
        //         continue;

        //     HealthCheckResultRequest defaultRequest = new HealthCheckResultRequest();
        //     defaultRequest.setDiagnosis("GOOD");
        //     defaultRequest.setLevel("GOOD");
        //     defaultRequest.setNote("No problem");
        //     defaultRequest.setHealthCheckFormId(form.getId());

        //     try {
        //         HealthCheckResultDTO resultDTO = createHealthCheckResult(defaultRequest);
        //         resultList.add(resultDTO);
        //     } catch (ResponseStatusException ex) {
        //         System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
        //     }
        // }

        // return resultList;
        return null;
    }

    public HealthCheckResultDTO updateHealthCheckResult(int healCheckResultId, HealthCheckResultRequest request) {

        // Optional<HealthCheckResultEntity> existingResultOpt = healthCheckResultRepository
        //         .findByHealthResultId(healCheckResultId);
        // if (existingResultOpt.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
        // }

        // Optional<HealthCheckFormEntity> healCheckFormOpt = healthCheckFormRepository
        //         .findById(request.getHealthCheckFormId());
        // if (healCheckFormOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");

        // HealthCheckFormEntity healthCheckFormEntity = healCheckFormOpt.get();

        // if (healthCheckFormEntity.getCommit() == null || !healthCheckFormEntity.getCommit()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Parent has not committed the health check form yet");
        // }

        // HealthCheckFormDTO healthCheckFormDTO = modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
        // HealthCheckResultEntity healthCheckResultEntity = existingResultOpt.get();
        // healthCheckResultEntity.setDiagnosis(request.getDiagnosis());
        // healthCheckResultEntity.setLevel(HealthCheckResultEntity.Level.valueOf(request.getLevel().toUpperCase()));
        // healthCheckResultEntity.setNote(request.getNote());
        // healthCheckResultEntity.setVision(request.getVision());
        // healthCheckResultEntity.setHearing(request.getHearing());
        // healthCheckResultEntity.setWeight(request.getWeight());
        // healthCheckResultEntity.setHeight(request.getHeight());
        // healthCheckResultRepository.save(healthCheckResultEntity);

        // try {
        //     MedicalRecordEntity medicalRecordEntity = medicalRecordsRepository
        //             .findMedicalRecordByStudent_Id(healthCheckFormEntity.getStudent().getId())
        //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                     "Not found medical record by student"));

        //     medicalRecordEntity.setVision(healthCheckResultEntity.getVision());
        //     medicalRecordEntity.setHearing(healthCheckResultEntity.getHearing());
        //     medicalRecordEntity.setWeight(healthCheckResultEntity.getWeight());
        //     medicalRecordEntity.setHeight(healthCheckResultEntity.getHeight());
        //     medicalRecordEntity.setCreateBy((byte) 1);
        //     medicalRecordsRepository.save(medicalRecordEntity);
        // } catch (Exception e) {
        //     throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating medical record", e);
        // }

        // HealthCheckResultDTO healthCheckResultDTO = modelMapper.map(healthCheckResultEntity,
        //         HealthCheckResultDTO.class);
        // healthCheckResultDTO.setHealthCheckFormDTO(healthCheckFormDTO);
        // healthCheckResultDTO.setStudentDTO(
        //         modelMapper.map(healthCheckFormEntity.getStudent(), StudentDTO.class));

        // return healthCheckResultDTO;
        return null;
    }

    public HealthCheckResultDTO getHealthCheckResult(int healthCheckResultId) {
        // HealthCheckResultEntity healthCheckResultEntity = healthCheckResultRepository
        //         .findByHealthResultId(healthCheckResultId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found"));
        // HealthCheckResultDTO dto = modelMapper.map(healthCheckResultEntity, HealthCheckResultDTO.class);

        // HealthCheckFormEntity formEntity = healthCheckResultEntity.getHealthCheckFormEntity();
        // HealthCheckFormDTO formDTO = modelMapper.map(formEntity, HealthCheckFormDTO.class);
        // dto.setHealthCheckFormDTO(formDTO);

        // StudentEntity student = formEntity.getStudent();
        // StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        // if (student.getClassEntity() != null) {
        //     studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
        // }

        // if (student.getParent() != null) {
        //     studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        // }

        // dto.setStudentDTO(studentDTO);

        // return dto;
        return null;
    }

    public List<HealthCheckResultDTO> getHealthCheckResultByProgram(int programId) {
        // List<HealthCheckResultEntity> resultEntityList = healthCheckResultRepository
        //         .findByHealthCheckFormEntity_HealthCheckProgram_Id(programId);

        // if (resultEntityList.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check result found for this program");
        // }

        // List<HealthCheckResultDTO> dtoList = new ArrayList<>();

        // for (HealthCheckResultEntity entity : resultEntityList) {
        //     HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

        //     HealthCheckFormEntity form = entity.getHealthCheckFormEntity();
        //     dto.setHealthCheckFormDTO(modelMapper.map(form, HealthCheckFormDTO.class));

        //     StudentEntity student = form.getStudent();
        //     StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        //     if (student.getClassEntity() != null)
        //         studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));

        //     if (student.getParent() != null)
        //         studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));

        //     dto.setStudentDTO(studentDTO);
        //     dtoList.add(dto);
        // }

        // return dtoList;
        return null;
    }

    public void deleteHealthCheckResult(int healthCheckResultId) {
        // Optional<HealthCheckResultEntity> healthCheckResultOpt = healthCheckResultRepository
        //         .findByHealthResultId(healthCheckResultId);
        // if (healthCheckResultOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check result not found");
        // HealthCheckResultEntity healthCheckResultEntity = healthCheckResultOpt.get();
        // healthCheckResultRepository.delete(healthCheckResultEntity);

    }

    public VaccineResultDTO createVaccineResult(VaccineResultRequest request) {

        // Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(request.getVaccineFormId());
        // if (vaccineFormOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");

        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

        // if (Boolean.FALSE.equals(vaccineFormEntity.getCommit())) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the vaccine form yet");
        // }

        // Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository
        //         .findByVaccineFormEntity(vaccineFormEntity);
        // if (existingResultOpt.isPresent()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Vaccine result already exists for this student: " + vaccineFormEntity.getStudent().getId());
        // }

        // int studentId = vaccineFormEntity.getStudent().getId();
        // MedicalRecordEntity medicalRecordEntity = medicalRecordsRepository
        //         .findMedicalRecordByStudent_Id(studentId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //                 "Not found medical record by student"));

        // VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
        // vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        // vaccineResultEntity.setResultNote(request.getResultNote());
        // vaccineResultEntity.setReaction(request.getReaction());
        // vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        // vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        // vaccineResultRepository.save(vaccineResultEntity);

        // VaccineProgramEntity program = vaccineFormEntity.getVaccineProgram();

        // VaccineHistoryEntity history = new VaccineHistoryEntity();
        // VaccineNameEntity vaccineNameEntity = program.getVaccineName();

        // if (vaccineNameEntity == null) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        //             "Vaccine program missing vaccine name entity");
        // }

        // history.setVaccineNameEntity(vaccineNameEntity);
        // history.setNote(DEFAULT_VACCINE_HS_NOTE);
        // history.setMedicalRecord(medicalRecordEntity);

        // vaccineHistoryRepository.save(history);

        // VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        // vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        // vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(), StudentDTO.class));
        // return vaccineResultDTO;
        return null;
    }

    public List<VaccineResultDTO> createDefaultVaccineResultsForAllCommittedForms() {
        // List<VaccineFormEntity> committedForms = vaccineFormRepository.findByCommitTrue();
        // List<VaccineResultDTO> resultList = new ArrayList<>();

        // for (VaccineFormEntity form : committedForms) {
        //     boolean exists = vaccineResultRepository.findByVaccineFormEntity(form).isPresent();
        //     if (exists)
        //         continue;

        //     VaccineResultRequest defaultRequest = new VaccineResultRequest();
        //     defaultRequest.setReaction("No reaction");
        //     defaultRequest.setStatusHealth("GOOD");
        //     defaultRequest.setResultNote("No problem");
        //     defaultRequest.setVaccineFormId(form.getId());

        //     try {
        //         VaccineResultDTO resultDTO = createVaccineResult(defaultRequest);
        //         resultList.add(resultDTO);
        //     } catch (ResponseStatusException ex) {
        //         System.out.println("Lỗi tạo result cho formId=" + form.getId() + ": " + ex.getReason());
        //     }
        // }

        // return resultList;
        return null;
    }

    public VaccineResultDTO updateVaccineResult(int vaccineResultId, VaccineResultRequest request) {
        // Optional<VaccineResultEntity> existingResultOpt = vaccineResultRepository.findById(vaccineResultId);
        // if (existingResultOpt.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        // }

        // Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(request.getVaccineFormId());
        // if (vaccineFormOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");

        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

        // if (vaccineFormEntity.getCommit() == null || !vaccineFormEntity.getCommit()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent has not committed the vaccine form yet");
        // }

        // VaccineResultEntity vaccineResultEntity = existingResultOpt.get();

        // vaccineResultEntity.setStatusHealth(request.getStatusHealth());
        // vaccineResultEntity.setResultNote(request.getResultNote());
        // vaccineResultEntity.setReaction(request.getReaction());
        // vaccineResultEntity.setCreatedAt(LocalDateTime.now());
        // vaccineResultEntity.setVaccineFormEntity(vaccineFormEntity);
        // vaccineResultRepository.save(vaccineResultEntity);

        // VaccineResultDTO vaccineResultDTO = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);
        // vaccineResultDTO.setVaccineFormDTO(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        // vaccineResultDTO.setStudentDTO(modelMapper.map(vaccineFormEntity.getStudent(), StudentDTO.class));

        // return vaccineResultDTO;
        return null;
    }

    public VaccineResultDTO getVaccineResult(int vaccineResultId) {
        // VaccineResultEntity vaccineResultEntity = vaccineResultRepository.findById(vaccineResultId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found"));

        // VaccineResultDTO dto = modelMapper.map(vaccineResultEntity, VaccineResultDTO.class);

        // VaccineFormEntity formEntity = vaccineResultEntity.getVaccineFormEntity();
        // VaccineFormDTO formDTO = modelMapper.map(formEntity, VaccineFormDTO.class);
        // dto.setVaccineFormDTO(formDTO);

        // StudentEntity student = formEntity.getStudent();
        // StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        // if (student.getClassEntity() != null) {
        //     studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));
        // }

        // if (student.getParent() != null) {
        //     studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));
        // }

        // dto.setStudentDTO(studentDTO);

        // return dto;
        return null;
    }

    public List<VaccineResultDTO> getVaccineResultByProgram(int programId) {
        // List<VaccineResultEntity> resultEntities = vaccineResultRepository
        //         .findByVaccineFormEntity_VaccineProgram_VaccineId(programId);

        // if (resultEntities.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine result found for this program");

        // List<VaccineResultDTO> dtoList = new ArrayList<>();
        // for (VaccineResultEntity entity : resultEntities) {
        //     VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

        //     VaccineFormEntity form = entity.getVaccineFormEntity();
        //     dto.setVaccineFormDTO(modelMapper.map(form, VaccineFormDTO.class));

        //     StudentEntity student = form.getStudent();
        //     StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

        //     if (student.getClassEntity() != null)
        //         studentDTO.setClassDTO(modelMapper.map(student.getClassEntity(), ClassDTO.class));

        //     if (student.getParent() != null)
        //         studentDTO.setUserDTO(modelMapper.map(student.getParent(), UserDTO.class));

        //     dto.setStudentDTO(studentDTO);
        //     dtoList.add(dto);
        // }

        // return dtoList;
        return null;
    }

    public void deleteVaccineResult(int vaccineResultId) {
        // Optional<VaccineResultEntity> vaccineResultOpt = vaccineResultRepository.findById(vaccineResultId);
        // if (vaccineResultOpt.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not found");
        // VaccineResultEntity vaccineResultEntity = vaccineResultOpt.get();
        // vaccineResultRepository.delete(vaccineResultEntity);

    }

    public List<StudentDTO> getAllStudent() {
        // List<StudentEntity> studentEntityList = studentRepository.findAll();
        // if (studentEntityList.isEmpty())
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found");

        // List<StudentDTO> studentDTOList = studentEntityList.stream().map(student -> {
        //     StudentDTO dto = modelMapper.map(student, StudentDTO.class);
        //     if (student.getClassEntity() != null) {
        //         ClassDTO classDTO = modelMapper.map(student.getClassEntity(), ClassDTO.class);
        //         dto.setClassDTO(classDTO);
        //     }
        //     if (student.getParent() != null) {
        //         UserDTO userDTO = modelMapper.map(student.getParent(), UserDTO.class);
        //         dto.setUserDTO(userDTO);
        //     }
        //     return dto;
        // }).collect(Collectors.toList());
        // return studentDTOList;
        return null;
    }

    public FeedbackDTO replyToFeedback(Integer feedbackId, ReplyFeedbackRequest request) {
        // FeedbackEntity feedback = feedbackRepository.findById(feedbackId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO RESPONSE."));

        // if (feedback.getStatus() == FeedbackStatus.REPLIED) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RESPONSE WAS REPLIED");
        // }

        // feedback.setResponseNurse(request.getResponse());
        // feedback.setStatus(FeedbackStatus.REPLIED);

        // if (request.getNurseId() != null) {
        //     UserEntity nurse = userRepository.findById(request.getNurseId())
        //             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND"));
        //     feedback.setNurse(nurse);
        // }

        // feedbackRepository.save(feedback);
        // FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        // return dto;
        return null;
    }

    public List<FeedbackDTO> getFeedbacksForNurse(Integer nurseId) {
        // UserEntity nurse = userRepository.findById(nurseId.intValue())
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NURSE NOT FOUND."));
        // List<FeedbackEntity> feedbackList = feedbackRepository.findByNurse(nurse);

        // List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
        //         .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class)).collect(Collectors.toList());

        // return feedbackDTOList;
        return null;
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        // List<FeedbackEntity> feedbackList = feedbackRepository.findAll();

        // List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
        //         .map(feedback -> {
        //             FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        //             dto.setParentId(feedback.getParent() != null ? feedback.getParent().getUserId() : null);
        //             dto.setNurseId(feedback.getNurse() != null ? feedback.getNurse().getUserId() : null);
        //             dto.setVaccineResultId(
        //                     feedback.getVaccineResult() != null ? feedback.getVaccineResult().getVaccineResultId()
        //                             : null);
        //             dto.setHealthResultId(
        //                     feedback.getHealthResult() != null ? feedback.getHealthResult().getHealthResultId() : null);
        //             return dto;
        //         })
        //         .collect(Collectors.toList());

        // return feedbackDTOList;
        return null;
    }

    public List<StudentDTO> getStudentsNotVaccinated(int vaccineProgramId, int vaccineNameId) {
        // List<StudentEntity> students;

        // if (vaccineProgramId != null) {
        //     students = studentRepository.findStudentsNeverVaccinatedByProgramId(vaccineProgramId);
        // } else if (vaccineNameId != null) {
        //     students = studentRepository.findStudentsNeverVaccinatedByVaccineNameId(vaccineNameId);
        // } else {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing filter condition");
        // }

        // return students.stream()
        //         .map(s -> modelMapper.map(s, StudentDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public List<ClassStudentDTO> getAllStudenttt() {
        List<ClassEntity> classList = classRepository.findAll();


        if (classList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No classes found");
        }
        List<ClassStudentDTO> classDTOList = new ArrayList<>();
        for (ClassEntity classEntity : classList) {
            List<StudentEntity> students = studentRepository.findByClassEntity(classEntity);


            List<StudentDTO> studentDTOs;
            if (students.isEmpty()) {
                studentDTOs = new ArrayList<>();
            } else {
                studentDTOs = students.stream().map(student -> {
                    StudentDTO dto = modelMapper.map(student, StudentDTO.class);
                    if (student.getClassEntity() != null) {
                        ClassDTO classDTO = modelMapper.map(student.getClassEntity(), ClassDTO.class);
                        dto.setClassDTO(classDTO);
                    }
                    if (student.getParent() != null) {
                        UserDTO userDTO = modelMapper.map(student.getParent(), UserDTO.class);
                        dto.setParentDTO(userDTO);
                    }
                    return dto;
                }).collect(Collectors.toList());
            }


            ClassStudentDTO classDTO = modelMapper.map(classEntity, ClassStudentDTO.class);
            classDTO.setStudents(studentDTOs);


            classDTOList.add(classDTO);
        }


        return classDTOList;


    }


    private String toSlug(String title) {
        // return title.toLowerCase()
        //         .replaceAll("[^a-z0-9\\s]", "")
        //         .replaceAll("\\s+", "-");
        return null;
    }

    public BlogResponse create(BlogRequest request) {
        // // Lấy thông tin người viết
        // UserEntity author = userRepository.findById(request.getUserId())
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // // Tạo bài viết mới
        // BlogEntity post = new BlogEntity();
        // post.setTitle(request.getTitle());
        // post.setSlug(toSlug(request.getTitle()));
        // post.setCategory(request.getCategory());
        // post.setContent(request.getContent());
        // post.setAuthor(author);

        // // Lưu vào database
        // BlogEntity saved = blogRepository.save(post);

        // return toResponse(saved);
        return null;
    }

    public List<BlogResponse> getAll() {
        // List<BlogEntity> posts = blogRepository.findAll();
        // return posts.stream().map(this::toResponse).collect(Collectors.toList());
        return null;
    }

    public BlogResponse getBySlug(String slug) {
        // BlogEntity post = blogRepository.findBySlug(slug)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        // return toResponse(post);
        return null;
    }

    public BlogResponse update(int id, BlogRequest request) {
        // BlogEntity post = blogRepository.findById(id)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // UserEntity author = userRepository.findById(request.getUserId())
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // post.setTitle(request.getTitle());
        // post.setSlug(toSlug(request.getTitle()));
        // post.setCategory(request.getCategory());
        // post.setContent(request.getContent());
        // post.setAuthor(author);

        // BlogEntity updated = blogRepository.save(post);
        // return toResponse(updated);
        return null;
    }

    public void delete(int id) {
        // blogRepository.deleteById(id);
    }

    private BlogResponse toResponse(BlogEntity post) {
        // BlogResponse response = modelMapper.map(post, BlogResponse.class);
        // response.setUserId(post.getAuthor().getUserId());
        // return response;
        return null;
    }

    public List<UserDTO> searchUsers(String keyword, UserRole roleFilter) {
        // // Lấy toàn bộ danh sách user từ database
        // List<UserEntity> userList = userRepository.findAll();

        // // Lọc bỏ user có role là ADMIN và theo điều kiện keyword/roleFilter
        // List<UserDTO> result = userList.stream()
        //         .filter(user -> user.getRole() != UserEntity.UserRole.ADMIN)
        //         .filter(user -> {
        //             boolean matchesKeyword = true;
        //             if (keyword != null && !keyword.isBlank()) {
        //                 String lowerKeyword = keyword.toLowerCase();
        //                 matchesKeyword = (user.getFullName() != null
        //                         && user.getFullName().toLowerCase().contains(lowerKeyword))
        //                         || (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword))
        //                         || (user.getPhone() != null && user.getPhone().toLowerCase().contains(lowerKeyword));
        //             }
        //             boolean matchesRole = (roleFilter == null || user.getRole() == roleFilter);
        //             return matchesKeyword && matchesRole;
        //         })
        //         .map(user -> modelMapper.map(user, UserDTO.class))
        //         .collect(Collectors.toList());

        // if (result.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No users found");
        // }

        // return result;
        return null;
    }

    public List<StudentDTO> getStudentsByClass(int classId) {
        // List<StudentEntity> students = studentRepository.findByClassEntity_ClassId(classId);
        // return students.stream()
        //         .map(student -> modelMapper.map(student, StudentDTO.class))
        //         .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRecordDTO> searchMedicalRecordsByStudentName(String keyword) {
        // List<StudentEntity> students = studentRepository.findByFullNameContainingIgnoreCase(keyword);

        // List<MedicalRecordDTO> medicalRecords = new ArrayList<>();

        // for (StudentEntity student : students) {
        //     Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository
        //             .findMedicalRecordByStudent_Id(student.getId());

        //     recordOpt.ifPresent(record -> {
        //         MedicalRecordDTO dto = modelMapper.map(record, MedicalRecordDTO.class);
        //         dto.setStudentId(student.getId());
        //         medicalRecords.add(dto);
        //     });
        // }

        // return medicalRecords;
        return null;
    }

    public List<MedicalRequestDTO> getByStatus(MedicalRequestEntity.MedicalRequestStatus status) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findByStatus(status);
        // return entities.stream()
        //         .map(this::convertToDTO)
        //         .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> getByDate(LocalDate date) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        //         .stream()
        //         .filter(r -> r.getDate().isEqual(date))
        //         .collect(Collectors.toList());
        // return entities.stream()
        //         .map(this::convertToDTO)
        //         .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> getByClass(int classId) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        //         .stream()
        //         .filter(r -> r.getStudent().getClassEntity().getClassId() == classId)
        //         .collect(Collectors.toList());
        // return entities.stream()
        //         .map(this::convertToDTO)
        //         .collect(Collectors.toList());
        return null;
    }

    public List<MedicalRequestDTO> searchByDateRange(LocalDate from, LocalDate to) {
        // List<MedicalRequestEntity> entities = medicalRequestRepository.findAll()
        //         .stream()
        //         .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
        //         .collect(Collectors.toList());
        // return entities.stream()
        //         .map(this::convertToDTO)
        //         .collect(Collectors.toList());
        return null;
    }

    public void updateStatus(int requestId, MedicalRequestEntity.MedicalRequestStatus newStatus) {
        // MedicalRequestEntity entity = medicalRequestRepository.findById(requestId)
        //         .orElseThrow(() -> new NoSuchElementException("Request not found"));
        // entity.setStatus(newStatus);
        // medicalRequestRepository.save(entity);
    }

    private MedicalRequestDTO convertToDTO(MedicalRequestEntity entity) {
        // MedicalRequestDTO dto = modelMapper.map(entity, MedicalRequestDTO.class);
        // dto.setStatus(entity.getStatus().name());
        // dto.setParentId(entity.getParent().getUserId());
        // return dto;
        return null;
    }

    public void createFormsForHealthCheckProgram(int programId) {
        // HealthCheckProgramEntity program = healthCheckProgramRepository.findById(programId)
        //         .orElseThrow(() -> new RuntimeException("Program not found"));

        // List<HealthCheckFormEntity> existingForms = healthCheckFormRepository.findByHealthCheckProgram_Id(programId);
        // for (HealthCheckFormEntity form : existingForms) {
        //     if (form.getStatus() == HealthCheckFormEntity.HealthCheckFormStatus.SENT) {
        //         throw new RuntimeException("Health check forms already created for this program");
        //     } else {
        //         form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
        //         healthCheckFormRepository.save(form);
        //     }
        // }

        // // List<StudentEntity> students = studentRepository.findAllWithParent();

        // // List<HealthCheckFormEntity> forms = new ArrayList<>();

        // // for (StudentEntity student : students) {
        // // HealthCheckFormEntity form = new HealthCheckFormEntity();
        // // form.setHealthCheckProgram(program);
        // // form.setStudent(student);
        // // form.setParent(student.getParent());
        // // form.setFormDate(LocalDate.now());
        // // form.setStatus(HealthCheckFormEntity.HealthCheckFormStatus.SENT);
        // // form.setCommit(false);
        // // forms.add(form);
        // // }

        // // healthCheckFormRepository.saveAll(forms);
    }

    public void createFormsForVaccineProgram(int vaccineProgramId) {
        // VaccineProgramEntity program = vaccineProgramRepository.findById(vaccineProgramId)
        //         .orElseThrow(() -> new RuntimeException("Vaccine program not found"));

        // List<StudentEntity> students = studentRepository.findAllWithParent();

        // List<VaccineFormEntity> forms = new ArrayList<>();

        // List<VaccineFormEntity> existingForms = vaccineFormRepository.findByVaccineProgram_VaccineId(vaccineProgramId);
        // for (VaccineFormEntity form : existingForms) {
        //     if (form.getStatus() == VaccineFormEntity.VaccineFormStatus.SENT) {
        //         throw new RuntimeException("Health check forms already created for this program");
        //     } else {
        //         form.setStatus(VaccineFormEntity.VaccineFormStatus.SENT);
        //         vaccineFormRepository.save(form);
        //     }
        // }
    }

    public List<HealthCheckResultDTO> createResultsByProgramId(int programId) {
        // logger.info("Creating health check results for program ID: {}", programId);
        // List<HealthCheckFormEntity> committedForms = healthCheckFormRepository.findCommittedFormsByProgramId(programId);
        // List<HealthCheckResultDTO> resultList = new ArrayList<>();

        // for (HealthCheckFormEntity form : committedForms) {
        //     Optional<HealthCheckResultEntity> existingResult = healthCheckResultRepository
        //             .findByHealthCheckFormEntity(form);
        //     if (existingResult.isPresent())
        //         continue;

        //     HealthCheckResultEntity result = new HealthCheckResultEntity();
        //     result.setHealthCheckFormEntity(form);
        //     result.setDiagnosis("Không");
        //     result.setLevel(HealthCheckResultEntity.Level.GOOD);
        //     result.setVision("10/10");
        //     result.setHearing("Bình thường");
        //     result.setWeight(0.0);
        //     result.setHeight(0.0);
        //     result.setNote("Không");

        //     HealthCheckResultEntity savedResult = healthCheckResultRepository.save(result);

        //     StudentEntity student = form.getStudent();

        //     Optional<MedicalRecordEntity> medicalRecordOpt = medicalRecordsRepository
        //             .findMedicalRecordByStudent_Id(student.getId());
        //     if (medicalRecordOpt.isEmpty()) {
        //         MedicalRecordEntity medicalRecord = new MedicalRecordEntity();
        //         medicalRecord.setStudent(student);
        //         medicalRecord.setVision(result.getVision());
        //         medicalRecord.setHearing(result.getHearing());
        //         medicalRecord.setWeight(result.getWeight());
        //         medicalRecord.setHeight(result.getHeight());
        //         medicalRecord.setNote(result.getNote());
        //         medicalRecord.setCreateBy((byte) 1);
        //         medicalRecord.setLastUpdate(LocalDateTime.now());
        //         medicalRecordsRepository.save(medicalRecord);
        //     } else {
        //         MedicalRecordEntity medicalRecord = medicalRecordOpt.get();

        //         medicalRecord.setVision(savedResult.getVision());
        //         medicalRecord.setHearing(savedResult.getHearing());
        //         medicalRecord.setWeight(savedResult.getWeight());
        //         medicalRecord.setHeight(savedResult.getHeight());
        //         medicalRecord.setNote(savedResult.getNote());
        //         medicalRecord.setCreateBy((byte) 1);
        //         medicalRecord.setLastUpdate(LocalDateTime.now());
        //         medicalRecordsRepository.save(medicalRecord);
        //     }

        //     HealthCheckResultDTO dto = new HealthCheckResultDTO();
        //     dto.setHealthResultId(savedResult.getHealthResultId());
        //     dto.setDiagnosis(savedResult.getDiagnosis());
        //     dto.setLevel(savedResult.getLevel().name());
        //     dto.setNote(savedResult.getNote());
        //     dto.setVision(savedResult.getVision());
        //     dto.setHearing(savedResult.getHearing());
        //     dto.setWeight(savedResult.getWeight());
        //     dto.setHeight(savedResult.getHeight());

        //     HealthCheckFormDTO formDTO = new HealthCheckFormDTO();
        //     formDTO.setId(form.getId());
        //     formDTO.setFormDate(form.getFormDate());
        //     formDTO.setCommit(form.getCommit());
        //     formDTO.setStatus(form.getStatus() != null ? form.getStatus().name() : null);
        //     dto.setHealthCheckFormDTO(formDTO);

        //     StudentDTO studentDTO = modelMapper.map(form.getStudent(), StudentDTO.class);
        //     dto.setStudentDTO(studentDTO);
        //     resultList.add(dto);
        // }
        // Optional<HealthCheckProgramEntity> programOpt = healthCheckProgramRepository.findById(programId);
        // programOpt.ifPresent(program -> {
        //     program.setStatus(HealthCheckProgramStatus.COMPLETED);
        //     healthCheckProgramRepository.save(program);
        // });
        // return resultList;
        return null;
    }

    public List<VaccineResultDTO> createVaccineResultsByProgramId(int programId) {
        // logger.info("Creating vaccine results for program ID: {}", programId);
        // List<VaccineFormEntity> committedForms = vaccineFormRepository.findCommittedFormsByProgramId(programId);
        // List<VaccineResultDTO> resultList = new ArrayList<>();

        // for (VaccineFormEntity form : committedForms) {
        //     Optional<VaccineResultEntity> existingResult = vaccineResultRepository.findByVaccineFormEntity(form);
        //     if (existingResult.isPresent())
        //         continue;

        //     VaccineResultEntity result = new VaccineResultEntity();
        //     result.setVaccineFormEntity(form);
        //     result.setStatusHealth("Tốt");
        //     result.setResultNote("Không");
        //     result.setReaction("Không");
        //     result.setCreatedAt(LocalDateTime.now());

        //     VaccineResultEntity savedResult = vaccineResultRepository.save(result);

        //     VaccineResultDTO dto = new VaccineResultDTO();
        //     dto.setVaccineResultId(savedResult.getVaccineResultId());
        //     dto.setStatusHealth(savedResult.getStatusHealth());
        //     dto.setResultNote(savedResult.getResultNote());
        //     dto.setReaction(savedResult.getReaction());
        //     dto.setCreatedAt(savedResult.getCreatedAt());

        //     VaccineFormDTO formDTO = new VaccineFormDTO();
        //     formDTO.setId(form.getId());
        //     formDTO.setStudentId(form.getStudent() != null ? form.getStudent().getId() : null);
        //     formDTO.setParentId(form.getParent() != null ? form.getParent().getUserId() : null);
        //     formDTO.setFormDate(form.getFormDate());
        //     formDTO.setNote(form.getNote());
        //     formDTO.setCommit(form.getCommit());
        //     formDTO.setStatus(form.getStatus() != null ? form.getStatus().name() : null);

        //     if (form.getVaccineProgram() != null) {
        //         VaccineProgramDTO vaccineProgramDTO = modelMapper.map(form.getVaccineProgram(),
        //                 VaccineProgramDTO.class);
        //         formDTO.setVaccineProgram(vaccineProgramDTO);
        //     }

        //     dto.setVaccineFormDTO(formDTO);

        //     if (form.getStudent() != null) {
        //         StudentDTO studentDTO = modelMapper.map(form.getStudent(), StudentDTO.class);
        //         dto.setStudentDTO(studentDTO);
        //     }

        //     resultList.add(dto);
        //     if (form.getVaccineProgram() != null && form.getVaccineProgram().getVaccineName() != null
        //             && form.getStudent() != null) {
        //         int studentId = form.getStudent().getId();
        //         Optional<MedicalRecordEntity> medicalRecordOpt = medicalRecordsRepository
        //                 .findMedicalRecordByStudent_Id(studentId);

        //         if (medicalRecordOpt.isPresent()) {
        //             MedicalRecordEntity medicalRecord = medicalRecordOpt.get();

        //             VaccineHistoryEntity history = new VaccineHistoryEntity();
        //             history.setVaccineNameEntity(form.getVaccineProgram().getVaccineName());
        //             history.setMedicalRecord(medicalRecord);
        //             // logger.info(medicalRecord.getRecordId() + " - " +
        //             // history.getVaccineNameEntity().getVaccineName());
        //             history.setNote(DEFAULT_VACCINE_HS_NOTE);
        //             history.setCreateBy((byte) 1);

        //             vaccineHistoryRepository.save(history);
        //         } else {
        //             MedicalRecordEntity medicalRecord = new MedicalRecordEntity();
        //             medicalRecord.setStudent(form.getStudent());
        //             medicalRecord.setCreateBy((byte) 1);
        //             medicalRecord.setLastUpdate(LocalDateTime.now());
        //             medicalRecordsRepository.save(medicalRecord);

        //             VaccineHistoryEntity history = new VaccineHistoryEntity();
        //             history.setVaccineNameEntity(form.getVaccineProgram().getVaccineName());
        //             history.setMedicalRecord(medicalRecord);
        //             history.setNote(DEFAULT_VACCINE_HS_NOTE);
        //             history.setCreateBy((byte) 1);

        //             vaccineHistoryRepository.save(history);
        //         }
        //     }

        // }

        // Optional<VaccineProgramEntity> programOpt = vaccineProgramRepository.findById(programId);
        // programOpt.ifPresent(program -> {
        //     program.setStatus(VaccineProgramStatus.COMPLETED);
        //     vaccineProgramRepository.save(program);
        // });

        // return resultList;
        return null;
    }

}
