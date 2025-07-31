package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.modules.users.dtos.request.*;
import com.swp391.school_medical_management.modules.users.dtos.response.*;
import com.swp391.school_medical_management.modules.users.entities.*;
import com.swp391.school_medical_management.modules.users.repositories.*;
import com.swp391.school_medical_management.modules.users.repositories.projection.EventStatRaw;
import com.swp391.school_medical_management.modules.users.repositories.projection.HealthCheckResultByProgramStatsRaw;
import com.swp391.school_medical_management.modules.users.repositories.projection.ParticipationRateRaw;
import com.swp391.school_medical_management.service.EmailService;
import com.swp391.school_medical_management.service.PasswordService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired
    private VaccineProgramRepository vaccineProgramRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MedicalRecordsRepository medicalRecordsRepository;

    @Autowired
    private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired
    private VaccineFormRepository vaccineFormRepository;

    @Autowired
    private MedicalEventRepository medicalEventRepository;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private MedicalRequestRepository medicalRequestRepository;

    @Autowired
    private VaccineResultRepository vaccineResultRepository;

    @Autowired
    private HealthCheckResultRepository healthCheckResultRepository;

    @Autowired
    private VaccineNameRepository vaccineNameRepository;

    @Autowired
    private ParticipateClassRepository participateClassRepository;

    @Autowired
    private VaccineUnitRepository vaccineUnitRepository;

    public HealthCheckProgramDTO createHealthCheckProgram(HealthCheckProgramRequest request, int adminId) {
        UserEntity admin = userRepository.findById(request.getAdminId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));

        UserEntity nurse = userRepository.findById(request.getNurseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository.findByHealthCheckNameAndStatus(request.getHealthCheckName(), HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED);

        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình '" + request.getHealthCheckName() + "' đã tồn tại và chưa bắt đầu.");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = new HealthCheckProgramEntity();
        healthCheckProgramEntity.setHealthCheckName(request.getHealthCheckName());
        healthCheckProgramEntity.setDescription(request.getDescription());
        healthCheckProgramEntity.setStartDate(request.getStartDate());
        healthCheckProgramEntity.setDateSendForm(request.getDateSendForm());
        healthCheckProgramEntity.setLocation(request.getLocation());
        healthCheckProgramEntity.setStatus(request.getStatus());
        healthCheckProgramEntity.setAdmin(admin);
        healthCheckProgramEntity.setNurse(nurse);

        healthCheckProgramRepository.save(healthCheckProgramEntity);

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setProgramId(healthCheckProgramEntity.getId());
                participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

                participateClassRepository.save(participate);
            }
        }

        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgram(int id, HealthCheckProgramRequest request) {
        HealthCheckProgramEntity existingProgram = healthCheckProgramRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe"));

        if (existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED || existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể cập nhật chương trình đã hoàn thành hoặc đang diễn ra");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        if (!existingProgram.getHealthCheckName().equals(request.getHealthCheckName())) {
            Optional<HealthCheckProgramEntity> duplicateProgramOpt = healthCheckProgramRepository.findByHealthCheckNameAndStatus(request.getHealthCheckName(), HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED);
            if (duplicateProgramOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình '" + request.getHealthCheckName() + "' đã tồn tại và chưa bắt đầu.");
            }
        }

        UserEntity admin = userRepository.findById(request.getAdminId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));
        existingProgram.setAdmin(admin);

        UserEntity nurse = userRepository.findById(request.getNurseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));
        existingProgram.setNurse(nurse);

        existingProgram.setHealthCheckName(request.getHealthCheckName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setStartDate(request.getStartDate());
        existingProgram.setDateSendForm(request.getDateSendForm());
        existingProgram.setLocation(request.getLocation());
        existingProgram.setStatus(request.getStatus());

        healthCheckProgramRepository.save(existingProgram);

        participateClassRepository.deleteByHealthCheckProgramId(existingProgram.getId());

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setProgramId(existingProgram.getId());
                participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

                participateClassRepository.save(participate);
            }
        }

        return modelMapper.map(existingProgram, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgramStatus(int id, String status) {
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe"));

        HealthCheckProgramEntity.HealthCheckProgramStatus newStatus;
        try {
            newStatus = HealthCheckProgramEntity.HealthCheckProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        }

        if (healthCheckProgramEntity.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình đã hoàn thành và không thể cập nhật");
        }

        if (healthCheckProgramEntity.getStatus() == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình đã ở trạng thái: " + newStatus);
        }

        if (newStatus == HealthCheckProgramEntity.HealthCheckProgramStatus.FORM_SENT) {
            createHealthCheckForm(healthCheckProgramEntity);
        }

        healthCheckProgramEntity.setStatus(newStatus);
        healthCheckProgramRepository.save(healthCheckProgramEntity);

        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public void createHealthCheckForm(HealthCheckProgramEntity programEntity) {
        String nurseEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity nurse = userRepository.findUserByEmail(nurseEmail).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với email: " + nurseEmail));

        List<StudentEntity> students = studentRepository.findAll();

        for (StudentEntity student : students) {
            UserEntity parent = student.getParent();
            if (parent == null) continue;

            List<HealthCheckFormEntity> existingForms = healthCheckFormRepository.findHealthCheckFormEntityByHealthCheckProgramAndStudent(programEntity, student);
            boolean hasUncommittedForm = existingForms.stream().anyMatch(form -> form.getCommit() == null);
            if (hasUncommittedForm) continue;

            HealthCheckFormEntity healthCheckFormEntity = new HealthCheckFormEntity();
            healthCheckFormEntity.setStudent(student);
            healthCheckFormEntity.setParent(parent);
            healthCheckFormEntity.setNotes(null);
            healthCheckFormEntity.setCommit(null);
            healthCheckFormEntity.setExpDate(programEntity.getStartDate().minusDays(7));
            healthCheckFormEntity.setHealthCheckProgram(programEntity);
            healthCheckFormEntity.setNurse(nurse);

            healthCheckFormRepository.save(healthCheckFormEntity);
        }
    }

    public List<HealthCheckProgramDTO> getAllHealthCheckProgram(int adminId) {
        List<HealthCheckProgramEntity> healthCheckProgramEntityList = healthCheckProgramRepository.findAll();

        if (healthCheckProgramEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }
        List<HealthCheckProgramDTO> healthCheckProgramDTOList = healthCheckProgramEntityList.stream().map(entity -> {
            HealthCheckProgramDTO dto = modelMapper.map(entity, HealthCheckProgramDTO.class);

            dto.setAdminId(entity.getAdmin().getUserId());
            dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
            dto.setStatus(entity.getStatus().name());

            UserDTO adminDTO = modelMapper.map(entity.getAdmin(), UserDTO.class);
            dto.setAdminDTO(adminDTO);

            if (entity.getNurse() != null) {
                UserDTO nurseDTO = modelMapper.map(entity.getNurse(), UserDTO.class);
                dto.setNurseDTO(nurseDTO);
            }

            List<ParticipateClassEntity> participateClassEntities = participateClassRepository.findByProgramIdAndType(entity.getId(), ParticipateClassEntity.Type.HEALTH_CHECK);

            List<ParticipateClassDTO> participateClassDTOS = participateClassEntities.stream().map(participateClassEntity -> {
                ParticipateClassDTO participateClassDTO = new ParticipateClassDTO();
                participateClassDTO.setParticipate_id(participateClassEntity.getParticipateId());
                participateClassDTO.setClass_id(participateClassEntity.getClazz().getClassId());
                participateClassDTO.setProgram_id(participateClassEntity.getProgramId());
                participateClassDTO.setType(participateClassEntity.getType().toString());

                ClassDTO classDTO = modelMapper.map(participateClassEntity.getClazz(), ClassDTO.class);
                classDTO.setStudents(null);

                participateClassDTO.setClassDTO(classDTO);
                return participateClassDTO;
            }).collect(Collectors.toList());

            dto.setParticipateClasses(participateClassDTOS);

            List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_Id(entity.getId());
            List<HealthCheckFormDTO> formDTOs = formEntities.stream().map(form -> modelMapper.map(form, HealthCheckFormDTO.class)).collect(Collectors.toList());
            dto.setHealthCheckFormDTOs(formDTOs);


            return dto;
        }).collect(Collectors.toList());


        return healthCheckProgramDTOList;
    }


    public HealthCheckProgramDTO getHealthCheckProgramById(int id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);

        if (healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        HealthCheckProgramEntity entity = healthCheckProgramOpt.get();

        HealthCheckProgramDTO dto = modelMapper.map(entity, HealthCheckProgramDTO.class);

        // Set thêm các field phụ
        dto.setAdminId(entity.getAdmin().getUserId());
        dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
        dto.setStatus(entity.getStatus().name());

        // Map adminDTO
        dto.setAdminDTO(modelMapper.map(entity.getAdmin(), UserDTO.class));

        // Map nurseDTO nếu có
        if (entity.getNurse() != null) {
            dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
        }

        // (Tùy chọn) Nếu bạn muốn map thêm healthCheckFormDTOs:
        List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_Id(id);
        List<HealthCheckFormDTO> formDTOs = formEntities.stream().map(form -> modelMapper.map(form, HealthCheckFormDTO.class)).collect(Collectors.toList());
        dto.setHealthCheckFormDTOs(formDTOs);

        // (Tùy chọn) Nếu bạn muốn map thêm participateClasses:
        List<ParticipateClassEntity> participateEntities = participateClassRepository.findByProgramId(id);
        List<ParticipateClassDTO> participateDTOs = participateEntities.stream().map(pc -> modelMapper.map(pc, ParticipateClassDTO.class)).collect(Collectors.toList());
        dto.setParticipateClasses(participateDTOs);

        return dto;
    }

    public void deleteHealthCheckProgram(int id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);

        if (healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        HealthCheckProgramEntity.HealthCheckProgramStatus status = healthCheckProgramEntity.getStatus();
        if (status == HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING || status == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xoá chương trình đang diễn ra hoặc đã hoàn thành");
        }

        healthCheckProgramRepository.delete(healthCheckProgramEntity);
    }

    public VaccineProgramDTO createVaccineProgram(VaccineProgramRequest request, long adminId) {
        // UserEntity admin = userRepository.findUserByUserId(adminId)
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin
        // not found"));

        // VaccineNameEntity vaccineNameEntity =
        // vaccineNameRepository.findById(request.getVaccineNameId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine name not found"));

        // Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository
        // .findByVaccineNameAndStatus(vaccineNameEntity,
        // VaccineProgramStatus.NOT_STARTED);

        // // Optional<VaccineProgramEntity> lastVaccineProgramOpt =
        // // vaccineProgramRepository
        // //
        // .findTopByVaccineDateLessThanEqualOrderByVaccineDateDesc(request.getVaccineDate());

        // // if (lastVaccineProgramOpt.isPresent()) {
        // // VaccineProgramEntity lastVaccineProgramEntity =
        // lastVaccineProgramOpt.get();
        // // logger.info(lastVaccineProgramEntity.getVaccineName().getVaccineName() + "
        // "
        // // + lastVaccineProgramEntity.getStatus());
        // // if
        // //
        // (!VaccineProgramStatus.COMPLETED.equals(lastVaccineProgramEntity.getStatus()))
        // // {
        // // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last vaccine is
        // // not COMPLETED!");
        // // }
        // // }

        // if (existingProgramOpt.isPresent()) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine program with name '" + vaccineNameEntity.getVaccineName()
        // + "' already exists and not started.");
        // }

        // VaccineProgramEntity entity = new VaccineProgramEntity();
        // entity.setVaccineName(vaccineNameEntity);
        // entity.setDescription(request.getDescription());
        // entity.setVaccineDate(request.getVaccineDate());
        // entity.setNote(request.getNote());
        // entity.setStatus(VaccineProgramStatus.NOT_STARTED);
        // entity.setAdmin(admin);

        // vaccineProgramRepository.save(entity);

        // VaccineNameDTO vaccineNameDTO = new VaccineNameDTO();
        // vaccineNameDTO.setId(vaccineNameEntity.getVaccineNameId());
        // vaccineNameDTO.setVaccineName(vaccineNameEntity.getVaccineName());
        // vaccineNameDTO.setManufacture(vaccineNameEntity.getManufacture());
        // vaccineNameDTO.setUrl(vaccineNameEntity.getUrl());
        // vaccineNameDTO.setNote(vaccineNameEntity.getNote());

        // VaccineProgramDTO dto = new VaccineProgramDTO();
        // dto.setVaccineId(entity.getVaccineId());
        // dto.setVaccineName(vaccineNameDTO);
        // dto.setDescription(entity.getDescription());
        // dto.setVaccineDate(entity.getVaccineDate());
        // dto.setStatus(entity.getStatus().name());
        // dto.setNote(entity.getNote());

        // return dto;
        return null;
    }

    public VaccineProgramDTO updateVaccineProgram(VaccineProgramRequest request, long id) {
        // Optional<VaccineProgramEntity> existingProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (existingProgramOpt.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // }

        // VaccineProgramEntity existingProgram = existingProgramOpt.get();

        // if (existingProgram.getStatus() == VaccineProgramStatus.COMPLETED
        // || existingProgram.getStatus() == VaccineProgramStatus.ON_GOING) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot update 'COMPLETED' or 'ON_GOING' program");
        // }

        // VaccineNameEntity vaccineNameEntity =
        // vaccineNameRepository.findById(request.getVaccineNameId())
        // .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Vaccine name not found"));

        // existingProgram.setVaccineName(vaccineNameEntity);
        // existingProgram.setDescription(request.getDescription());
        // existingProgram.setVaccineDate(request.getVaccineDate());
        // existingProgram.setNote(request.getNote());

        // vaccineProgramRepository.save(existingProgram);

        // return modelMapper.map(existingProgram, VaccineProgramDTO.class);
        return null;
    }

    public VaccineProgramDTO updateVaccineProgramStatus(Long id, String status) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findById(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        // VaccineProgramStatus newStatus;
        // try {
        // newStatus = VaccineProgramStatus.valueOf(status.toUpperCase());
        // } catch (IllegalArgumentException e) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: "
        // + status);
        // }

        // if (vaccineProgramEntity.getStatus() == VaccineProgramStatus.COMPLETED) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Program is already COMPLETED and cannot be updated");
        // }

        // if (vaccineProgramEntity.getStatus() == (newStatus)) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is already
        // in status: " + newStatus);
        // }

        // if (newStatus == VaccineProgramStatus.COMPLETED
        // && vaccineProgramEntity.getStatus() != VaccineProgramStatus.ON_GOING) {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        // "Cannot mark as COMPLETED unless the program is ON_GOING");
        // }

        // vaccineProgramEntity.setStatus(newStatus);
        // vaccineProgramRepository.save(vaccineProgramEntity);

        // if (newStatus == VaccineProgramStatus.ON_GOING) {
        // createVaccineForm(vaccineProgramEntity);
        // }

        // return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
        return null;
    }

    public void createVaccineForm(VaccineProgramEntity programEntity) {
        // VaccineNameEntity vaccineNameEntity = programEntity.getVaccineName();
        // Long programId = programEntity.getVaccineId();

        // List<StudentEntity> students =
        // studentRepository.findStudentsNeverVaccinatedByProgramId(programId);

        // for (StudentEntity studentEntity : students) {
        // UserEntity parent = studentEntity.getParent();
        // if (parent == null)
        // continue;

        // List<VaccineFormEntity> existingForms = vaccineFormRepository
        // .findVaccineFormEntityByVaccineProgramAndStudent(programEntity,
        // studentEntity);

        // boolean hasUncommittedForm = existingForms.stream()
        // .anyMatch(form -> form.getCommit() == null);
        // if (hasUncommittedForm)
        // continue;

        // VaccineFormEntity vaccineFormEntity = new VaccineFormEntity();
        // vaccineFormEntity.setStudent(studentEntity);
        // vaccineFormEntity.setParent(parent);
        // vaccineFormEntity.setNote(null);
        // vaccineFormEntity.setCommit(null);
        // vaccineFormEntity.setFormDate(programEntity.getVaccineDate().minusDays(7));
        // vaccineFormEntity.setStatus(VaccineFormStatus.DRAFT);
        // vaccineFormEntity.setVaccineProgram(programEntity);

        // vaccineFormRepository.save(vaccineFormEntity);
        // }
    }

    public List<VaccineProgramDTO> getAllVaccineProgram() {
        List<VaccineProgramEntity> vaccineProgramEntitieList = vaccineProgramRepository.findAll();
        if (vaccineProgramEntitieList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình tiêm vaccine nào!");
        }
        List<VaccineProgramDTO> vaccineProgramDTOList = vaccineProgramEntitieList.stream().map(entity -> {
            VaccineProgramDTO dto = modelMapper.map(entity, VaccineProgramDTO.class);
            dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
            dto.setAdminDTO(modelMapper.map(entity.getAdmin(), UserDTO.class));
            dto.setVaccineNameDTO(modelMapper.map(entity.getVaccineName(), VaccineNameDTO.class));


            List<ParticipateClassEntity> participateClassEntities = participateClassRepository.findByProgramIdAndType(entity.getVaccineId(), ParticipateClassEntity.Type.VACCINE);

            List<ParticipateClassDTO> participateClassDTOS = participateClassEntities.stream().map(participateClassEntity -> {
                ParticipateClassDTO participateClassDTO = new ParticipateClassDTO();
                participateClassDTO.setParticipate_id(participateClassEntity.getParticipateId());
                participateClassDTO.setClass_id(participateClassEntity.getClazz().getClassId());
                participateClassDTO.setProgram_id(participateClassEntity.getProgramId());
                participateClassDTO.setType(participateClassEntity.getType().toString());

                ClassDTO classDTO = modelMapper.map(participateClassEntity.getClazz(), ClassDTO.class);
                classDTO.setStudents(null);

                participateClassDTO.setClassDTO(classDTO);
                return participateClassDTO;
            }).collect(Collectors.toList());

            dto.setParticipateClassDTOs(participateClassDTOS);

            List<VaccineFormDTO> vaccineFormDTOS = vaccineFormRepository.findByVaccineProgram(entity).stream().map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class)).collect(Collectors.toList());

            dto.setVaccineFormDTOs(vaccineFormDTOS);

            return dto;
        }).collect(Collectors.toList());


        return vaccineProgramDTOList;
    }

    public VaccineProgramDTO getVaccineProgramById(long id) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        // return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
        return null;
    }

    public void deleteVaccineProgram(long id) {
        // Optional<VaccineProgramEntity> vaccineProgramOpt =
        // vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        // if (vaccineProgramOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not
        // found");
        // VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        // vaccineProgramRepository.delete(vaccineProgramEntity);
    }

    public List<ClassDTO> getAllClass() {
        // List<ClassEntity> classEntityList = classRepository.findAll();
        // if (classEntityList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        // List<ClassDTO> classDTOList = classEntityList.stream()
        // .map(classEntity -> modelMapper.map(classEntity, ClassDTO.class))
        // .collect(Collectors.toList());
        // return classDTOList;
        return null;
    }

    public List<StudentDTO> getAllStudentInClass(Long classId) {
        // Optional<ClassEntity> classOpt = classRepository.findByClassId(classId);
        // if (classOpt.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        // ClassEntity classEntity = classOpt.get();

        // List<StudentEntity> studentEntitieList =
        // studentRepository.findByClassEntity(classEntity);
        // if (studentEntitieList.isEmpty())
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class is empty");

        // List<StudentDTO> studentDTOList = studentEntitieList.stream()
        // .map(studentEntitie -> modelMapper.map(studentEntitie, StudentDTO.class))
        // .collect(Collectors.toList());
        // return studentDTOList;
        return null;
    }

    //Thien
    public MedicalRecordDTO getMedicalRecordByStudentId(int parentId, int studentId) {
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository.findByStudent_Id(studentId);
        if (optMedicalRecord.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ y tế cho học sinh với ID: " + studentId);
        MedicalRecordEntity medicalRecord = optMedicalRecord.get();
        return modelMapper.map(medicalRecord, MedicalRecordDTO.class);
        // return null;
    }


    //Thien
    public UserDTO createAccount(NurseAccountRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại, Vui lòng sử dụng email khác");
        }
        String password = passwordService.generateStrongRandomPassword();
        String encodedPassword = passwordEncoder.encode(password);
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setFullName(request.getName());
        user.setPassword(encodedPassword);
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setActive(true);
        user.setRole(UserEntity.UserRole.NURSE);
        userRepository.save(user);
        emailService.sendEmail(request.getEmail(), "Thông tin tài khoản", "Tài khoản của bạn đã được tạo thành công.\n\n" + "Email: " + request.getEmail() + "\n" + "Password: " + password + "\n\n" + "Vui lòng đăng nhập và đổi mật khẩu ngay để bảo mật tài khoản.");
        return modelMapper.map(user, UserDTO.class);
        // return null;
    }


    public List<UserDTO> getAllAccounts() {
        List<UserEntity> userEntities = userRepository.findAll();
        if (userEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản nào");
        }
        return userEntities.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
        // return null;
    }


    //Thien
    public UserDTO updateAccount(int userId, UpdateProfileRequest request) {
        UserEntity user = userRepository.findUserByUserId(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        user.setFullName(request.getFullName());
        // if (!user.getEmail().equals(request.getEmail())) {
        // boolean emailExists = userRepository.existsByEmail(request.getEmail());
        // if (emailExists) {
        // throw new RuntimeException("Email already in use");
        // }
        user.setEmail(request.getEmail());
        // }


        // if (!user.getPhone().equals(request.getPhone())) {
        // boolean phoneExists = userRepository.existsByPhone(request.getPhone());
        // if (phoneExists) {
        // throw new RuntimeException("Phone already in use");
        // }
        user.setPhone(request.getPhone());
        // }
        user.setAddress(request.getAddress());


        userRepository.save(user);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
        // return null;
    }


    public void deleteAccount(Long userId, String token) {
        // Optional<UserEntity> userOpt = userRepository.findUserByUserId(userId);
        // if (userOpt.isEmpty()) {
        // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        // }
        // UserEntity user = userOpt.get();
        // if (user.getRole() == UserRole.ADMIN) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài
        // khoản ADMIN");
        // }

        // if (user.getRole() == UserRole.PARENT) {
        // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thể xoá tài
        // khoản PHỤ HUYNH");
        // }

        // BlacklistTokenRequest blacklistRequest = new BlacklistTokenRequest();
        // blacklistRequest.setToken(token);

        // refreshTokenRepository.deleteByUser(user);
        // blacklistService.create(blacklistRequest);

        // userRepository.delete(user);
    }

    @Transactional
    public void importStudentFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            int importedCount = 0;

            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum == 0) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                String studentName = row.getCell(0).getStringCellValue().trim();
                LocalDate dob = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
                String genderStr = row.getCell(2).getStringCellValue().trim().toUpperCase(); // MALE/FEMALE
                StudentEntity.Gender gender = StudentEntity.Gender.valueOf(genderStr);
                String className = row.getCell(3).getStringCellValue().trim();
                String parentName = row.getCell(4).getStringCellValue().trim();
                String relationship = row.getCell(5).getStringCellValue().trim();
                String parentPhone = formatter.formatCellValue(row.getCell(6)).trim();
                String parentEmail = row.getCell(7).getStringCellValue().trim();
                String parentAddress = row.getCell(8).getStringCellValue().trim();

                Optional<ClassEntity> classOpt = classRepository.findByClassName(className);
                if (classOpt.isEmpty()) {
                    throw new RuntimeException("Không tìm thấy lớp \"" + className + "\" tại dòng " + (rowNum + 1));
                }
                ClassEntity classEntity = classOpt.get();

                UserEntity parent;
                Optional<UserEntity> parentOpt = userRepository.findUserByEmailOrPhone(parentEmail, parentPhone);
                if (parentOpt.isPresent()) {
                    parent = parentOpt.get(); // Dùng lại phụ huynh cũ
                } else {
                    // Tạo mới phụ huynh
                    parent = new UserEntity();
                    parent.setFullName(parentName);
                    parent.setEmail(parentEmail);
                    parent.setPhone(parentPhone);
                    parent.setAddress(parentAddress);
                    parent.setRelationship(relationship);
                    parent.setActive(true);
                    parent.setRole(UserEntity.UserRole.PARENT);
                    parent = userRepository.save(parent);
                }


                // Tạo student
                Optional<StudentEntity> existingStudentOpt = studentRepository.findByFullNameAndDobAndClassEntityAndParent(studentName, dob, classEntity, parent);
                if (existingStudentOpt.isPresent()) {
                    continue; // Bỏ qua học sinh đã tồn tại
                }
                StudentEntity student = new StudentEntity();
                student.setFullName(studentName);
                student.setDob(dob);
                student.setGender(gender);
                student.setClassEntity(classEntity);
                student.setParent(parent);
                studentRepository.save(student);

                importedCount++;
            }

            if (importedCount == 0) {
                throw new RuntimeException("Không có dòng nào được thêm vào.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Import thất bại: " + e.getMessage());
        }
    }


    public long countStudents() {
        return studentRepository.count();
    }

    public long countMedicalRecords() {
        return medicalRecordsRepository.count();
    }

    public long countVaccineProgram() {
        return vaccineProgramRepository
                .countByStatusIn((List.of(VaccineProgramEntity.VaccineProgramStatus.NOT_STARTED,
                        VaccineProgramEntity.VaccineProgramStatus.ON_GOING)));
    }

    public long countHealthCheckProgram() {
        return healthCheckProgramRepository
                .countByStatusIn((List.of(HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED,
                        HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING)));
    }

    public long countProcessingMedicalRequest() {
        return medicalRequestRepository.countByStatusIn((List.of(MedicalRequestEntity.MedicalRequestStatus.PROCESSING)));
    }

    // Thien
    public List<Map<String, Object>> getEventStatsByMonth(int year) {
        List<EventStatRaw> rawList = medicalEventRepository.getEventStatsByMonth(year);
        Map<Integer, Map<String, Object>> grouped = new TreeMap<>();

        for (EventStatRaw raw : rawList) {
            Integer month = raw.getMonth(); // 1 → 12
            String type = raw.getTypeEvent();
            int count = raw.getCount().intValue();

            Map<String, Object> row = grouped.computeIfAbsent(month, k -> {
                Map<String, Object> newRow = new HashMap<>();
                newRow.put("month", "T" + k); // Đổi tên theo format T1, T2,...
                return newRow;
            });

            row.put(type, count);
        }
        return new ArrayList<>(grouped.values());
    }

    // Thien
    public List<HealthCheckResultStatsDTO> getVaccineResultStatusStatsByProgram() {
        List<HealthCheckResultByProgramStatsRaw> rawList = vaccineResultRepository.getVaccineResultStatusStatsByProgram();

        return rawList.stream().map(row -> new HealthCheckResultStatsDTO(row.getProgramId(), row.getProgramName(), row.getStatusHealth(), row.getCount())).collect(Collectors.toList());
    }


    public List<HealthCheckResultStatsDTO> getHealthCheckResultStatusStatsByProgram() {
        List<HealthCheckResultByProgramStatsRaw> rawList = healthCheckResultRepository.getHealthCheckResultStatusStatsByProgram();


        return rawList.stream().map(row -> new HealthCheckResultStatsDTO(row.getProgramId(), row.getProgramName(), row.getStatusHealth(), row.getCount())).collect(Collectors.toList());
        // return null;
    }


    public ParticipationDTO getLatestParticipation() {
        logger.info("getLatestParticipation");
        ParticipationDTO participationDTO = new ParticipationDTO();
        Optional<VaccineProgramEntity> lastestVaccineProgramOpt = vaccineProgramRepository.findTopByStatusOrderByStartDateDesc(VaccineProgramEntity.VaccineProgramStatus.COMPLETED);
        Optional<HealthCheckProgramEntity> latestHealthCheckProgramOpt = healthCheckProgramRepository.findTopByStatusOrderByStartDateDesc(HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED);

        if (!lastestVaccineProgramOpt.isPresent()) {
            participationDTO.setVaccination(new CommitedPercentDTO(null, 0L, 0L));
        } else {
            VaccineProgramEntity latestVaccineProgram = lastestVaccineProgramOpt.get();
            ParticipationRateRaw vaccine = vaccineFormRepository.getParticipationRateByVaccineId(latestVaccineProgram.getVaccineId());
            participationDTO.setVaccination(new CommitedPercentDTO(latestVaccineProgram.getVaccineName().getVaccineName(), vaccine.getCommittedCount(), vaccine.getTotalSent()));
        }

        if (!latestHealthCheckProgramOpt.isPresent()) {
            participationDTO.setHealthCheck(new CommitedPercentDTO(null, 0L, 0L));
        } else {
            HealthCheckProgramEntity latestHealthCheckProgram = latestHealthCheckProgramOpt.get();
            ParticipationRateRaw healthCheck = healthCheckFormRepository.getParticipationRateByHealthCheckId(latestHealthCheckProgram.getId());
            participationDTO.setHealthCheck(new CommitedPercentDTO(latestHealthCheckProgram.getHealthCheckName(), healthCheck.getCommittedCount(), healthCheck.getTotalSent()));
        }
        return participationDTO;
    }

    public VaccineFormStatsDTO getFormStatsByProgram(Long vaccineProgramId) {
        // long total =
        // vaccineFormRepository.countByVaccineProgram_VaccineId(vaccineProgramId);
        // long committed =
        // vaccineFormRepository.countByVaccineProgram_VaccineIdAndCommitTrue(vaccineProgramId);

        // return new VaccineFormStatsDTO(total, committed);
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

    public VaccineNameDTO createVaccineName(VaccineNameRequest request) {
        // if
        // (vaccineNameRepository.findByVaccineName(request.getVaccineName()).isPresent())
        // {
        // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine name
        // already exists");
        // }

        // VaccineNameEntity entity = modelMapper.map(request, VaccineNameEntity.class);
        // VaccineNameEntity saved = vaccineNameRepository.save(entity);

        // return modelMapper.map(saved, VaccineNameDTO.class);
        return null;
    }

    public void importVaccineNameFromExcel(MultipartFile file) {
        // try (InputStream is = file.getInputStream(); Workbook workbook = new
        // XSSFWorkbook(is)) {
        // Sheet sheet = workbook.getSheetAt(0);
        // int importedCount = 0;
        // for (Row row : sheet) {
        // int rowNum = row.getRowNum();
        // if (rowNum == 0)
        // continue;
        // if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK)
        // continue;
        // try {

        // String vaccineName = row.getCell(0).getStringCellValue();
        // String manufacture = row.getCell(1).getStringCellValue();
        // String url = row.getCell(2).getStringCellValue();
        // String note = row.getCell(3).getStringCellValue();

        // VaccineNameEntity vaccine = new VaccineNameEntity();
        // vaccine.setVaccineName(vaccineName);
        // vaccine.setManufacture(manufacture);
        // vaccine.setUrl(url);
        // vaccine.setNote(note);

        // // student
        // Optional<VaccineNameEntity> vaccineNameOpt = vaccineNameRepository
        // .findByVaccineNameAndManufactureAndUrlAndNote(vaccineName, manufacture, url,
        // note);
        // VaccineNameEntity vaccineNameEntity;
        // if (vaccineNameOpt.isPresent()) {
        // vaccineNameEntity = vaccineNameOpt.get();
        // } else {
        // vaccineNameEntity = new VaccineNameEntity();
        // vaccineNameEntity.setVaccineName(vaccineName);
        // vaccineNameEntity.setManufacture(manufacture);
        // vaccineNameEntity.setUrl(url);
        // vaccineNameEntity.setNote(note);
        // }

        // vaccineNameRepository.save(vaccineNameEntity);
        // importedCount++;
        // } catch (Exception e) {
        // throw new RuntimeException("Lỗi ở dòng Excel số " + rowNum + ": "
        // + row.getCell(0).getStringCellValue() + " - " + e.getMessage());
        // }
        // }
        // if (importedCount == 0) {
        // throw new RuntimeException("File không hợp lệ.");
        // }
        // } catch (Exception e) {
        // throw new RuntimeException(e.getMessage());
        // }
    }
}
