package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.CommitHealthCheckFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.CommitVaccineFormRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.FeedbackRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRecordsRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.MedicalRequestDetailRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineHistoryRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.AllFormsByStudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.ClassDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.FeedbackDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalEventDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRecordDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.StudentDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.UserDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineHistoryDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineNameDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineResultDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineUnitDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity.HealthCheckProgramStatus;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckResultEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalEventEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRecordEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity.MedicalRequestStatus;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineHistoryEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineResultEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineUnitEntity;
import com.swp391.school_medical_management.modules.users.repositories.FeedbackRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalEventRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRecordsRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineHistoryRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineNameRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineResultRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineUnitRepository;
import com.swp391.school_medical_management.service.UploadImageFile;

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

    @Autowired
    private VaccineHistoryRepository vaccineHistoryRepository;

    @Autowired
    private VaccineUnitRepository vaccineUnitRepository;

    @Autowired
    private UploadImageFile uploadImageFile;

    public MedicalRecordDTO createMedicalRecord(int parentId, MedicalRecordsRequest request) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(request.getStudentId());
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh");

        StudentEntity student = studentOpt.get();
        if (!(student.getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập học sinh này");
        }

        Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository.findByStudent_Id(request.getStudentId());
        if (recordOpt.isPresent()) {
            throw new RuntimeException("Hồ sơ ý tế đã tồn tại");
        }

        MedicalRecordEntity medicalRecord = new MedicalRecordEntity();
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setChronicDisease(request.getChronicDisease());
        medicalRecord.setVision(request.getVision());
        medicalRecord.setHearing(request.getHearing());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setHeight(request.getHeight());
        medicalRecord.setLastUpdate(LocalDateTime.now());
        medicalRecord.setCreateBy(false);
        medicalRecord.setNote(request.getNote());
        medicalRecord.setStudent(student);

        List<VaccineHistoryEntity> vaccineHistoryEntities = new ArrayList<>();
        for (VaccineHistoryRequest vaccineHistoryRequest : request.getVaccineHistories()) {
            VaccineHistoryEntity vaccineHistory = new VaccineHistoryEntity();

            int vaccineNameId = vaccineHistoryRequest.getVaccineNameId();
            VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(vaccineNameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tồn tại vaccine"));

            vaccineHistory.setNote(vaccineHistoryRequest.getNote());
            vaccineHistory.setCreateBy(false);
            vaccineHistory.setUnit(vaccineHistoryRequest.getUnit());
            vaccineHistory.setStudent(student);
            vaccineHistory.setVaccineNameEntity(vaccineNameEntity);
            vaccineHistoryEntities.add(vaccineHistory);
            vaccineHistoryRepository.save(vaccineHistory);
        }

        medicalRecordsRepository.save(medicalRecord);

        List<VaccineHistoryDTO> vaccineHistoryDTOS = vaccineHistoryEntities.stream().map(vaccineHistory -> modelMapper.map(vaccineHistory, VaccineHistoryDTO.class)).collect(Collectors.toList());

        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
        MedicalRecordDTO medicalRecordDTO = modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        medicalRecordDTO.setStudentDTO(studentDTO);
        medicalRecordDTO.setVaccineHistoryDTOS(vaccineHistoryDTOS);
        return medicalRecordDTO;
    }

    public MedicalRecordDTO updateMedicalRecord(int parentId, MedicalRecordsRequest request) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(request.getStudentId());
        if (studentOpt.isEmpty() || !(studentOpt.get().getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào học sinh này!");
        }
        StudentDTO studentDTO = modelMapper.map(studentOpt.get(), StudentDTO.class);
        Optional<MedicalRecordEntity> recordOpt = medicalRecordsRepository.findByStudent_Id(request.getStudentId());
        if (recordOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ y tế!");
        }

        MedicalRecordEntity medicalRecord = recordOpt.get();
        medicalRecord.setAllergies(request.getAllergies());
        medicalRecord.setChronicDisease(request.getChronicDisease());
        medicalRecord.setVision(request.getVision());
        medicalRecord.setHearing(request.getHearing());
        medicalRecord.setWeight(request.getWeight());
        medicalRecord.setHeight(request.getHeight());
        medicalRecord.setLastUpdate(LocalDateTime.now());
        medicalRecord.setCreateBy(false);
        medicalRecord.setNote(request.getNote());
        medicalRecord.setStudent(studentOpt.get());
        medicalRecordsRepository.save(medicalRecord);

        List<VaccineHistoryEntity> vaccineHistoryEntities = vaccineHistoryRepository.findByStudent(studentOpt.get());
        List<VaccineHistoryRequest> incomingVaccines = request.getVaccineHistories();
        logger.info("incomingVaccines: " + incomingVaccines.size());

        for (VaccineHistoryEntity existingVaccine : vaccineHistoryEntities) {
            boolean stillExists = incomingVaccines.stream().anyMatch(req ->
                    req.getVaccineNameId() == existingVaccine.getVaccineNameEntity().getVaccineNameId()
                            && req.getUnit() == existingVaccine.getUnit()
                            && Objects.equals(req.getNote(), existingVaccine.getNote())
            );
            if (!stillExists) {
                vaccineHistoryRepository.delete(existingVaccine);
            }
        }
        for (VaccineHistoryRequest vaccineReq : incomingVaccines) {
            if (vaccineReq.getVaccineNameId() < 0) continue;

            // Kiểm tra vaccine đã tồn tại y hệt chưa (cùng vaccineNameId, unit, note)
            boolean alreadyExists = vaccineHistoryEntities.stream().anyMatch(db -> db.getVaccineNameEntity().getVaccineNameId() == vaccineReq.getVaccineNameId() && db.getUnit() == vaccineReq.getUnit() && Objects.equals(db.getNote(), vaccineReq.getNote()));

            if (alreadyExists) continue; // Đã có rồi thì bỏ qua

            // Nếu chưa có y hệt thì tạo mới bản ghi
            VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(vaccineReq.getVaccineNameId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy vaccine!"));

            VaccineHistoryEntity newVaccine = new VaccineHistoryEntity();
            newVaccine.setVaccineNameEntity(vaccineNameEntity);
            newVaccine.setUnit(vaccineReq.getUnit());
            newVaccine.setNote(vaccineReq.getNote());
            newVaccine.setCreateBy(false);
            newVaccine.setStudent(studentOpt.get());
            vaccineHistoryRepository.save(newVaccine);
        }

        List<VaccineHistoryEntity> updatedHistories = vaccineHistoryRepository.findByStudent(studentOpt.get());
        List<VaccineHistoryDTO> historyDTOS = updatedHistories.stream().map(vaccineHistory -> modelMapper.map(vaccineHistory, VaccineHistoryDTO.class)).collect(Collectors.toList());

        MedicalRecordDTO medicalRecordDTO = modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        medicalRecordDTO.setStudentDTO(studentDTO);
        medicalRecordDTO.setVaccineHistoryDTOS(historyDTOS);
        return medicalRecordDTO;
    }

    public List<MedicalRecordDTO> getAllMedicalRecordByParentId(int parentId) {
        // List<MedicalRecordEntity> medicalRecordList = medicalRecordsRepository
        // .findMedicalRecordByStudent_Parent_UserId(parentId);
        // return medicalRecordList.stream().map(medicalRecord ->
        // modelMapper.map(medicalRecord, MedicalRecordDTO.class))
        // .collect(Collectors.toList());
        return null;
    }

    public MedicalRecordDTO getMedicalRecordByStudentId(int parentId, int studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository.findByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ học sinh!");
        }

        MedicalRecordEntity medicalRecord = optMedicalRecord.get();

        if (!(medicalRecord.getStudent().getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập hồ sở này");
        }

        List<VaccineHistoryDTO> vaccineHistoryDTOList = vaccineHistoryRepository.findByStudent(medicalRecord.getStudent()).stream().map(vaccineHistory -> {
            VaccineHistoryDTO dto = new VaccineHistoryDTO();
            dto.setId(vaccineHistory.getId() != 0 ? vaccineHistory.getId() : null);
            dto.setNote(vaccineHistory.getNote());
            dto.setCreateBy(vaccineHistory.isCreateBy());
            dto.setUnit(vaccineHistory.getUnit());
            dto.setStudentId(vaccineHistory.getStudent().getId());
            dto.setStudentDTO(modelMapper.map(vaccineHistory.getStudent(), StudentDTO.class));
            dto.setVaccineNameDTO(modelMapper.map(vaccineHistory.getVaccineNameEntity(), VaccineNameDTO.class));
            return dto;
        }).collect(Collectors.toList());

        StudentDTO studentDTO = modelMapper.map(medicalRecord.getStudent(), StudentDTO.class);

        MedicalRecordDTO medicalRecordDTO = modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        medicalRecordDTO.setVaccineHistoryDTOS(vaccineHistoryDTOList);
        medicalRecordDTO.setStudentDTO(studentDTO);
        return medicalRecordDTO;
    }

    public void deleteMedicalRecord(int parentId, int studentId) {
        // Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository
        // .findMedicalRecordByStudent_Id(studentId);
        // if (optMedicalRecord.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not
        // found");
        // MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        // if (!medicalRecord.getStudent().getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // medicalRecordsRepository.delete(medicalRecord);
    }

    public MedicalRequestDTO createMedicalRequest(int parentId, MedicalRequest request, MultipartFile image) {

        String imageUrl = null;
        try {
            imageUrl = uploadImageFile.uploadImage(image);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Optional<StudentEntity> studentOpt = studentRepository.findStudentById(request.getStudentId());
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        StudentEntity student = studentOpt.get();
        UserEntity parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found"));

        if (request.getMedicalRequestDetailRequests() == null ||
                request.getMedicalRequestDetailRequests().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request details cannot be empty");
        }

        if (request.getDate() == null ||
                request.getDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request date must be today or in the future");
        }

        MedicalRequestEntity medicalRequestEntity = new MedicalRequestEntity();
        medicalRequestEntity.setRequestName(request.getRequestName());
        medicalRequestEntity.setDate(request.getDate());
        medicalRequestEntity.setStatus(MedicalRequestStatus.PROCESSING);
        medicalRequestEntity.setNote(request.getNote());
        medicalRequestEntity.setStudent(student);
        medicalRequestEntity.setParent(parent);
        medicalRequestEntity.setImage(imageUrl); 

        medicalRequestEntity.setMedicalRequestDetailEntities(new ArrayList<>());

        for (MedicalRequestDetailRequest details : request.getMedicalRequestDetailRequests()) {
            MedicalRequestDetailEntity medicalRequestDetailEntity = new MedicalRequestDetailEntity();
            medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
            medicalRequestDetailEntity.setQuantity(details.getQuantity());
            medicalRequestDetailEntity.setType(details.getType());
            medicalRequestDetailEntity.setTimeSchedule(details.getTimeSchedule());
            medicalRequestDetailEntity.setStatus(MedicalRequestDetailEntity.Status.NOT_TAKEN);
            medicalRequestDetailEntity.setNote(details.getNote());
            medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);

            medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        }

        medicalRequestRepository.save(medicalRequestEntity);
        MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity,
                MedicalRequestDTO.class);
        List<MedicalRequestDetailDTO> medicalRequestDetailDTOList = medicalRequestEntity
                .getMedicalRequestDetailEntities()
                .stream()
                .map(medicalRequestDetailEntity -> modelMapper.map(medicalRequestDetailEntity,
                        MedicalRequestDetailDTO.class))
                .collect(Collectors.toList());
        medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        return medicalRequestDTO;
    }


    public List<MedicalRequestDTO> getMedicalRequestByParent(int parentId) {
        // Optional<UserEntity> parentOpt = userRepository.findById(parentId);
        // if (parentOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found");
        // UserEntity parent = parentOpt.get();
        // List<MedicalRequestEntity> medicalRequestEntityList =
        // medicalRequestRepository.findByParent(parent);
        // if (medicalRequestEntityList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found medical
        // request");

        // List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        // for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
        // MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity,
        // MedicalRequestDTO.class);

        // // Map chi tiết đơn thuốc
        // List<MedicalRequestDetailDTO> detailDTOs =
        // medicalRequestEntity.getMedicalRequestDetailEntities()
        // .stream()
        // .map(detail -> modelMapper.map(detail, MedicalRequestDetailDTO.class))
        // .collect(Collectors.toList());
        // medicalRequestDTO.setMedicalRequestDetailDTO(detailDTOs);

        // // Map student nếu cần
        // // if (medicalRequestEntity.getStudent() != null) {
        // // StudentDTO studentDTO = modelMapper.map(medicalRequestEntity.getStudent(),
        // // StudentDTO.class);
        // // medicalRequestDTO.setStudentDTO(studentDTO);
        // // }

        // medicalRequestDTOList.add(medicalRequestDTO);
        // }
        // return medicalRequestDTOList;
        return null;
    }

    public MedicalRequestDTO getMedicalRequestByRequestId(int parentId, int requestId) {
        // Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository
        // .findMedicalRequestEntityByRequestId(requestId);
        // if (medicalRequestOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not
        // found");
        // MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        // List<MedicalRequestDetailEntity> medicalRequestDetailEntityList =
        // medicalRequest
        // .getMedicalRequestDetailEntities();
        // if (!medicalRequest.getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // List<MedicalRequestDetailDTO> medicalRequestDetailDTOList =
        // medicalRequestDetailEntityList.stream()
        // .map(medicalRequestDetailEntity ->
        // modelMapper.map(medicalRequestDetailEntity,
        // MedicalRequestDetailDTO.class))
        // .collect(Collectors.toList());
        // MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequest,
        // MedicalRequestDTO.class);
        // medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        // if (medicalRequest.getStudent() != null) {
        // StudentDTO studentDTO = modelMapper.map(medicalRequest.getStudent(),
        // StudentDTO.class);
        // medicalRequestDTO.setStudentDTO(studentDTO);
        // }
        // return medicalRequestDTO;
        return null;
    }

    public List<MedicalRequestDTO> getMedicalRequestByStudent(int parentId, int studentId) {
        // Optional<StudentEntity> studentOpt =
        // studentRepository.findStudentById(studentId);
        // if (studentOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        // StudentEntity student = studentOpt.get();
        // if (!student.getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // List<MedicalRequestEntity> medicalRequestEntityList =
        // medicalRequestRepository
        // .findMedicalRequestEntityByStudent(student);
        // List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        // for (MedicalRequestEntity medicalRequestEntity : medicalRequestEntityList) {
        // List<MedicalRequestDetailEntity> medicalRequestDetailEntityList =
        // medicalRequestEntity
        // .getMedicalRequestDetailEntities();
        // List<MedicalRequestDetailDTO> medicalRequestDetailDTOList =
        // medicalRequestDetailEntityList.stream()
        // .map(medicalRequestDetailEntity ->
        // modelMapper.map(medicalRequestDetailEntity,
        // MedicalRequestDetailDTO.class))
        // .collect(Collectors.toList());
        // MedicalRequestDTO medicalRequestDTO = modelMapper.map(medicalRequestEntity,
        // MedicalRequestDTO.class);
        // medicalRequestDTO.setMedicalRequestDetailDTO(medicalRequestDetailDTOList);
        // medicalRequestDTOList.add(medicalRequestDTO);
        // }
        // return medicalRequestDTOList;
        return null;
    }

    public MedicalRequestDTO updateMedicalRequest(int parentId, MedicalRequest request, Integer requestId) {
        // Optional<MedicalRequestEntity> medicalRequestEntityOpt =
        // medicalRequestRepository
        // .findMedicalRequestEntityByRequestId(requestId);
        // if (medicalRequestEntityOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not
        // found");
        // MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        // if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // if
        // (!medicalRequestEntity.getStatus().equals(MedicalRequestStatus.PROCESSING)) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot update a request that has already been processed");
        // }
        // if (request.getMedicalRequestDetailRequests() == null ||
        // request.getMedicalRequestDetailRequests().isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical request
        // details cannot be empty");
        // }
        // medicalRequestEntity.setRequestName(request.getRequestName());
        // medicalRequestEntity.setNote(request.getNote());
        // medicalRequestEntity.setDate(request.getDate());

        // medicalRequestEntity.getMedicalRequestDetailEntities().clear();

        // for (MedicalRequestDetailRequest details :
        // request.getMedicalRequestDetailRequests()) {
        // MedicalRequestDetailEntity medicalRequestDetailEntity = new
        // MedicalRequestDetailEntity();
        // medicalRequestDetailEntity.setMedicineName(details.getMedicineName());
        // medicalRequestDetailEntity.setDosage(details.getDosage());
        // medicalRequestDetailEntity.setTime(details.getTime());
        // medicalRequestDetailEntity.setMedicalRequest(medicalRequestEntity);
        // medicalRequestEntity.getMedicalRequestDetailEntities().add(medicalRequestDetailEntity);
        // }

        // medicalRequestRepository.save(medicalRequestEntity);
        // return modelMapper.map(medicalRequestEntity, MedicalRequestDTO.class);
        return null;
    }

    public void deleteMedicalRequest(int parentId, int requestId) {
        // Optional<MedicalRequestEntity> medicalRequestEntityOpt =
        // medicalRequestRepository
        // .findMedicalRequestEntityByRequestId(requestId);
        // if (medicalRequestEntityOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not
        // found");
        // MedicalRequestEntity medicalRequestEntity = medicalRequestEntityOpt.get();
        // if (!medicalRequestEntity.getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // if
        // (!medicalRequestEntity.getStatus().equals(MedicalRequestStatus.PROCESSING)) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot delete a request that has already been processed");
        // }
        // medicalRequestRepository.delete(medicalRequestEntity);
    }

    public List<HealthCheckFormDTO> getAllHealthCheckFormCommited(int parentId, int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh");

        StudentEntity studentEntity = studentOpt.get();

        List<HealthCheckFormEntity> healthCheckFormEntities = healthCHeckFormRepository
                .findAllByStudentAndCommitIsTrue(studentEntity);

        List<HealthCheckFormDTO> healthCheckFormDTOs = healthCheckFormEntities.stream()
                .filter(form -> form.getParent().getUserId() == parentId)
                .filter(form -> form.getHealthCheckProgram().getStatus() == HealthCheckProgramStatus.FORM_SENT)
                .map(form -> {
                    HealthCheckFormDTO dto = modelMapper.map(form, HealthCheckFormDTO.class);

                    if (form.getHealthCheckProgram() != null) {
                        HealthCheckProgramDTO programDTO = modelMapper.map(form.getHealthCheckProgram(),
                                HealthCheckProgramDTO.class);

                        if (form.getHealthCheckProgram().getAdmin() != null) {
                            UserDTO adminDTO = modelMapper.map(form.getHealthCheckProgram().getAdmin(), UserDTO.class);
                            programDTO.setAdminDTO(adminDTO);
                        }

                        if (form.getHealthCheckProgram().getNurse() != null) {
                            UserDTO nurseDTO = modelMapper.map(form.getHealthCheckProgram().getNurse(), UserDTO.class);
                            programDTO.setNurseDTO(nurseDTO);
                        }

                        dto.setHealthCheckProgramDTO(programDTO);
                    }

                    if (form.getParent() != null) {
                        UserDTO parentDTO = modelMapper.map(form.getParent(), UserDTO.class);
                        dto.setParentDTO(parentDTO);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return healthCheckFormDTOs;
    }

    public List<VaccineFormDTO> getAllVaccineFormCommited(int parentId, int studentId) {
        // Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        // if (studentOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        // StudentEntity studentEntity = studentOpt.get();
        // List<VaccineFormEntity> vaccineFormEntities =
        // vaccineFormRepository.findAllByStudentAndStatusAndCommitIsTrue(studentEntity,
        // VaccineFormStatus.SENT);

        // List<VaccineFormDTO> vaccineFormDTOs = vaccineFormEntities.stream()
        // .filter(form -> form.getStudent().getParent().getUserId().equals(parentId))
        // .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity,
        // VaccineFormDTO.class))
        // .collect(Collectors.toList());
        // return vaccineFormDTOs;
        return null;
    }

    public List<HealthCheckFormDTO> getAllHealthCheckForm(int parentId, int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh!");
        StudentEntity studentEntity = studentOpt.get();
        List<HealthCheckFormEntity> healthCheckFormEntities = healthCHeckFormRepository.findByStudent(studentEntity);

        List<HealthCheckFormDTO> healthCheckFormDTOs = healthCheckFormEntities.stream().filter(form -> form.getParent().getUserId() == parentId).map(form -> {
            HealthCheckFormDTO dto = modelMapper.map(form, HealthCheckFormDTO.class);
            HealthCheckProgramDTO programDTO = modelMapper.map(form.getHealthCheckProgram(), HealthCheckProgramDTO.class);
            programDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));
            dto.setHealthCheckProgramDTO(programDTO);
            return dto;
        }).collect(Collectors.toList());
        return healthCheckFormDTOs;
    }

    public List<VaccineFormDTO> getAllVaccineForm(int parentId, int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh!");
        StudentEntity studentEntity = studentOpt.get();
        List<VaccineFormEntity> vaccineFormEntities = vaccineFormRepository.findByStudent(studentEntity);

        List<VaccineFormDTO> vaccineFormDTOs = vaccineFormEntities.stream().filter(form -> form.getStudent().getParent().getUserId() == parentId).map(form -> {
            VaccineFormDTO dto = modelMapper.map(form, VaccineFormDTO.class);
            VaccineProgramDTO programDTO = modelMapper.map(form.getVaccineProgram(), VaccineProgramDTO.class);
            programDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));
            dto.setVaccineProgramDTO(programDTO);
            return dto;
        }).collect(Collectors.toList());

        return vaccineFormDTOs;
    }

    public HealthCheckFormDTO getHealthCheckForm(int parentId, int healthCheckFormId) {
        HealthCheckFormEntity form = healthCHeckFormRepository.findById(healthCheckFormId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy form khám sức khỏe."));

        if (form.getParent() == null || form.getParent().getUserId() != parentId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền truy cập form này.");
        }

        HealthCheckFormDTO dto = modelMapper.map(form, HealthCheckFormDTO.class);

        if (form.getStudent() != null) {
            StudentDTO studentDTO = modelMapper.map(form.getStudent(), StudentDTO.class);
            dto.setStudentDTO(studentDTO);
        }

        if (form.getParent() != null) {
            UserDTO parentDTO = modelMapper.map(form.getParent(), UserDTO.class);
            dto.setParentDTO(parentDTO);
        }

        if (form.getNurse() != null) {
            UserDTO nurseDTO = modelMapper.map(form.getNurse(), UserDTO.class);
            dto.setNurseDTO(nurseDTO);
        }

        if (form.getHealthCheckProgram() != null) {
            HealthCheckProgramDTO programDTO = modelMapper.map(form.getHealthCheckProgram(),
                    HealthCheckProgramDTO.class);

            if (form.getHealthCheckProgram().getAdmin() != null) {
                UserDTO adminDTO = modelMapper.map(form.getHealthCheckProgram().getAdmin(), UserDTO.class);
                programDTO.setAdminDTO(adminDTO);
            }

            if (form.getHealthCheckProgram().getNurse() != null) {
                UserDTO nurseProgramDTO = modelMapper.map(form.getHealthCheckProgram().getNurse(), UserDTO.class);
                programDTO.setNurseDTO(nurseProgramDTO);
            }

            dto.setHealthCheckProgramDTO(programDTO);
        }

        return dto;
    }

    public VaccineFormDTO getVaccineForm(int parentId, int vaccineFormId) {
        // Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository
        // .findByIdAndStatus(vaccineFormId, VaccineFormStatus.SENT);
        // if (vaccineFormOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form
        // not found");
        // VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        // if (vaccineFormEntity.getParent() == null
        // || !vaccineFormEntity.getParent().getUserId().equals(parentId))
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        // VaccineFormDTO vaccineFormDTO = modelMapper.map(vaccineFormEntity,
        // VaccineFormDTO.class);
        // return vaccineFormDTO;
        return null;
    }

    public List<HealthCheckResultDTO> getHealthCheckResults(int studentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh"));

        List<HealthCheckResultEntity> healthCheckResultList = healthCheckResultRepository.findByStudent(student);

        if (healthCheckResultList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có kết quả khám sức khỏe nào!");
        }

        List<HealthCheckResultDTO> healthCheckResultDTOList = healthCheckResultList.stream().map(entity -> {
            HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

            HealthCheckFormEntity form = entity.getHealthCheckForm();
            if (form != null) {
                HealthCheckFormDTO formDTO = modelMapper.map(form, HealthCheckFormDTO.class);

                if (form.getHealthCheckProgram() != null) {
                    formDTO.setHealthCheckProgramDTO(
                            modelMapper.map(form.getHealthCheckProgram(), HealthCheckProgramDTO.class));
                }
                if (form.getNurse() != null) {
                    formDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));
                }
                dto.setHealthCheckFormDTO(formDTO);
            }
            dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
            return dto;
        }).collect(Collectors.toList());
        return healthCheckResultDTOList;
    }


    public List<VaccineResultDTO> getVaccineResults(int studentId) {
        StudentEntity student = studentRepository.findById(studentId).get();
        List<VaccineResultEntity> vaccineResultList = vaccineResultRepository.findByStudentEntity(student);
        if (vaccineResultList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có kết quả vaccine nào");
        }
        List<VaccineResultDTO> vaccineResultDTOList = vaccineResultList.stream().map(entity -> {
            VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);
            VaccineFormEntity form = entity.getVaccineFormEntity();

            VaccineFormDTO vaccineFormDTO = modelMapper.map(form, VaccineFormDTO.class);

            vaccineFormDTO.setVaccineProgramDTO(modelMapper.map(form.getVaccineProgram(), VaccineProgramDTO.class));
            vaccineFormDTO.setVaccineNameDTO(modelMapper.map(form.getVaccineName(), VaccineNameDTO.class));
            vaccineFormDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));

            dto.setVaccineFormDTO(vaccineFormDTO);
            dto.setStudentDTO(vaccineFormDTO.getStudentDTO());
            return dto;
        }).collect(Collectors.toList());
        return vaccineResultDTOList;
    }

    public void commitHealthCheckForm(int parentId, int healthCheckFormId, CommitHealthCheckFormRequest request) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCHeckFormRepository.findById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo khám định kỳ!");
        }
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        if (healthCheckFormEntity.getParent() == null || !(healthCheckFormEntity.getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền để xác nhận thông báo khám định kỳ này!");
        }
        if (healthCheckFormEntity.getCommit() != null && healthCheckFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thông báo khám định kỳ này đã được xác nhận!");
        }
        healthCheckFormEntity.setCommit(request.isCommit());
        healthCheckFormEntity.setNotes(request.getNote());
        healthCHeckFormRepository.save(healthCheckFormEntity);
    }

    public void commitVaccineForm(int parentId, int vaccineFormId, CommitVaccineFormRequest request) {
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findById(vaccineFormId);
        if (vaccineFormOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo tiêm chủng nào!");
        }
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if (vaccineFormEntity.getParent() == null || !(vaccineFormEntity.getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền để xác nhận thông báo tiêm vaccine này!");
        }
        if (vaccineFormEntity.getCommit() != null && vaccineFormEntity.getCommit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thông báo tiêm vaccine này đã được xác nhận!");
        }
        vaccineFormEntity.setCommit(request.isCommit());
        vaccineFormEntity.setNote(request.getNote());
        vaccineFormRepository.save(vaccineFormEntity);
    }

    public void submitFeedback(FeedbackRequest request) {
        // UserEntity parent = userRepository.findById(request.getParentId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T
        // FIND TO PARENT"));

        // FeedbackEntity feedback = FeedbackEntity.builder()
        // .satisfaction(FeedbackEntity.Satisfaction.valueOf(request.getSatisfaction().toUpperCase()))
        // .comment(request.getComment())
        // .createdAt(LocalDateTime.now())
        // .status(FeedbackStatus.NOT_REPLIED)
        // .parent(parent)
        // .build();
        // if (request.getVaccineResultId() != null) {
        // VaccineResultEntity vr =
        // vaccineResultRepository.findById(request.getVaccineResultId())
        // .orElseThrow(
        // () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T FIND TO
        // VACCINE RESULT"));
        // feedback.setVaccineResult(vr);
        // }
        // if (request.getHealthResultId() != null) {
        // HealthCheckResultEntity hr =
        // healthCheckResultRepository.findById(request.getHealthResultId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
        // "DON'T FIND TO HEALTH CHECK RESULT"));
        // feedback.setHealthResult(hr);
        // }
        // feedbackRepository.save(feedback);
    }

    public List<FeedbackDTO> getFeedbacksByParent(int parentId) {
        // UserEntity parent = userRepository.findById(parentId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DON'T
        // FIND TO PARENT"));
        // List<FeedbackEntity> feedbackList = feedbackRepository.findByParent(parent);
        // if (feedbackList.isEmpty()) {
        // return Collections.emptyList();
        // }

        // List<FeedbackDTO> feedbackDTOList = feedbackList.stream()
        // .map(feedback -> modelMapper.map(feedback,
        // FeedbackDTO.class)).collect(Collectors.toList());

        // return feedbackDTOList;
        return null;
    }

    public List<MedicalEventDTO> getMedicalEventsByStudent(int parentId, int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh");

        StudentEntity studentEntity = studentOpt.get();

        if (!(studentEntity.getParent().getUserId() == parentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập học sinh này");

        List<MedicalEventEntity> medicalEventEntitieList = medicalEventRepository.findByStudent(studentEntity);
        if (medicalEventEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không có sự kiện y tế nào xảy ra!");

        List<MedicalEventDTO> medicalEventDTOList = medicalEventEntitieList.stream().map(medicalEventEntity -> {
            MedicalEventDTO dto = modelMapper.map(medicalEventEntity, MedicalEventDTO.class);

            dto.setStudentDTO(modelMapper.map(studentEntity, StudentDTO.class));
            dto.setParentDTO(modelMapper.map(studentEntity.getParent(), UserDTO.class));
            dto.setNurseDTO(modelMapper.map(medicalEventEntity.getNurse(), UserDTO.class));
            dto.setClassDTO(modelMapper.map(studentEntity.getClass(), ClassDTO.class));

            return dto;
        }).collect(Collectors.toList());
        return medicalEventDTOList;
    }

    public AllFormsByStudentDTO getAllFormByStudent(int parentId, int studentId) {
        Optional<StudentEntity> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh!");
        }
        StudentEntity student = studentOpt.get();
        if (!(student.getParent().getUserId() == parentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào học sinh này!");
        }

        AllFormsByStudentDTO allFormsByStudentDTO = new AllFormsByStudentDTO();

        List<HealthCheckFormDTO> healthCheckForms = getAllHealthCheckForm(parentId, studentId);
        if (!healthCheckForms.isEmpty()) {
            allFormsByStudentDTO.setHealthCheckForms(healthCheckForms);
        } else {
            allFormsByStudentDTO.setHealthCheckForms(Collections.emptyList());
        }

        List<VaccineFormDTO> vaccineForms = getAllVaccineForm(parentId, studentId);

        //Thien lay them VaccineUnitDTO cho VaccineFormDTO
        for (VaccineFormDTO vaccineForm : vaccineForms) {
            VaccineProgramDTO programDTO = vaccineForm.getVaccineProgramDTO();
            if (programDTO != null) {
                VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(programDTO.getVaccineId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vaccine!"));
                programDTO.setVaccineNameDTO(modelMapper.map(vaccineNameEntity, VaccineNameDTO.class));

                VaccineNameDTO vaccineNameDTO = modelMapper.map(vaccineNameEntity, VaccineNameDTO.class);

                List<VaccineUnitEntity> vaccineUnitEntities = vaccineUnitRepository.findByVaccineName_VaccineNameId(vaccineNameDTO.getId());
                List<VaccineUnitDTO> vaccineUnitDTOS = vaccineUnitEntities.stream()
                        .map(vaccineUnitEntity -> modelMapper.map(vaccineUnitEntity, VaccineUnitDTO.class))
                        .collect(Collectors.toList());
                vaccineNameDTO.setVaccineUnitDTOs(vaccineUnitDTOS);


                programDTO.setVaccineNameDTO(vaccineNameDTO);
            }
        }

        if (!vaccineForms.isEmpty()) {
            allFormsByStudentDTO.setVaccineForms(vaccineForms);
        } else {
            allFormsByStudentDTO.setVaccineForms(Collections.emptyList());
        }
        return allFormsByStudentDTO;
    }

    public HealthCheckResultDTO getHealthCheckResultByFormId(int formId) {
    Optional<HealthCheckResultEntity> healthCheckResultOpt =
        healthCheckResultRepository.findByHealthCheckForm_Id(formId);

    if (healthCheckResultOpt.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Không tìm thấy kết quả khám sức khỏe với formId: " + formId);
    }

    HealthCheckResultEntity entity = healthCheckResultOpt.get();
    HealthCheckResultDTO dto = modelMapper.map(entity, HealthCheckResultDTO.class);

    HealthCheckFormEntity form = entity.getHealthCheckForm();
    HealthCheckFormDTO formDTO = modelMapper.map(form, HealthCheckFormDTO.class);

    formDTO.setHealthCheckProgramDTO(
        modelMapper.map(form.getHealthCheckProgram(), HealthCheckProgramDTO.class));

    if (form.getNurse() != null) {
        formDTO.setNurseDTO(modelMapper.map(form.getNurse(), UserDTO.class));
    }

    dto.setHealthCheckFormDTO(formDTO);
    dto.setStudentDTO(modelMapper.map(form.getStudent(), StudentDTO.class));

    return dto;
}


    public VaccineResultDTO getVaccineResultByFormId(int formId) {
        // Optional<VaccineResultEntity> vaccineResultOpt =
        // vaccineResultRepository.findByVaccineFormEntity_Id(formId);

        // if (vaccineResultOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine result not
        // found for formId: " + formId);

        // VaccineResultEntity entity = vaccineResultOpt.get();
        // VaccineResultDTO dto = modelMapper.map(entity, VaccineResultDTO.class);

        // VaccineFormEntity form = entity.getVaccineFormEntity();
        // dto.setVaccineFormDTO(modelMapper.map(form, VaccineFormDTO.class));
        // dto.setStudentDTO(modelMapper.map(form.getStudent(), StudentDTO.class));

        // return dto;
        return null;
    }

    public List<VaccineNameDTO> getAllVaccineNames() {
        List<VaccineNameEntity> vaccineNameEntities = vaccineNameRepository.findAll();
        List<VaccineNameDTO> vaccineNameDTOS = new ArrayList<>();
        for (VaccineNameEntity entity : vaccineNameEntities) {
            VaccineNameDTO vaccineNameDTO = modelMapper.map(entity, VaccineNameDTO.class);

            List<VaccineUnitEntity> vaccineUnitEntities = vaccineUnitRepository.findByVaccineName_VaccineNameId(entity.getVaccineNameId());
            List<VaccineUnitDTO> vaccineUnitDTOS = vaccineUnitEntities.stream().map(vaccineUnitEntity -> modelMapper.map(vaccineUnitEntity, VaccineUnitDTO.class)).collect(Collectors.toList());

            UserDTO userDTO = modelMapper.map(entity.getUser(), UserDTO.class);

            vaccineNameDTO.setVaccineUnitDTOs(vaccineUnitDTOS);
            vaccineNameDTO.setUserDTO(userDTO);
            vaccineNameDTOS.add(vaccineNameDTO);
        }
        return vaccineNameDTOS;
    }
}
