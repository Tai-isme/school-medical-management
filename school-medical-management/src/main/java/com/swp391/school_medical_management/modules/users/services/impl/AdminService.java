package com.swp391.school_medical_management.modules.users.services.impl;

import com.swp391.school_medical_management.helpers.ExcelExportStyleUtil;
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
import org.apache.poi.ss.util.CellRangeAddress;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Autowired
    private VaccineHistoryRepository vaccineHistoryRepository;

    public HealthCheckProgramDTO createHealthCheckProgram(HealthCheckProgramRequest request, int adminId) {
        UserEntity admin = userRepository.findById(request.getAdminId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));

        UserEntity nurse = userRepository.findById(request.getNurseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        if (request.getStartDate().isBefore(request.getDateSendForm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày gửi thông báo phải sớm hơn ngày chương trình được diễn ra!");
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

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setProgramId(healthCheckProgramEntity.getId());
                participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

                ParticipateClassEntity saved = participateClassRepository.save(participate);

                ParticipateClassDTO participateDTO = modelMapper.map(saved, ParticipateClassDTO.class);
                participateDTO.setClass_id(clazz.getClassId());
                participateDTO.setProgram_id(healthCheckProgramEntity.getId());
                participateDTO.setType(saved.getType().toString());

                ClassDTO classDTO = modelMapper.map(clazz, ClassDTO.class);
                participateDTO.setClassDTO(classDTO);

                participateClassDTOs.add(participateDTO);
            }
        }

        UserDTO adminDTO = modelMapper.map(admin, UserDTO.class);
        UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);

        HealthCheckProgramDTO programDTO = modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
        programDTO.setParticipateClasses(participateClassDTOs);
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());

        return programDTO;
    }


    public HealthCheckProgramDTO updateHealthCheckProgram(int id, HealthCheckProgramRequest request) {
        HealthCheckProgramEntity existingProgram = healthCheckProgramRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe"));

        if (existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED || existingProgram.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể cập nhật chương trình đã hoàn thành hoặc đang diễn ra!");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải là hôm nay hoặc trong tương lai!");
        }

        if (request.getStartDate().isBefore(request.getDateSendForm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày gửi thông báo phải sớm hơn ngày chương trình được diễn ra!");
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
                participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);
                participate.setProgramId(existingProgram.getId());

                participateClassRepository.save(participate);
            }
        }

        UserDTO adminDTO = modelMapper.map(admin, UserDTO.class);
        UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);

        List<ParticipateClassEntity> participateEntities = participateClassRepository.findAllByProgramId(existingProgram.getId());

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();

        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO dto = modelMapper.map(participate, ParticipateClassDTO.class);
            dto.setProgram_id(existingProgram.getId());
            dto.setClass_id(participate.getClazz().getClassId());

            ClassDTO classDTO = modelMapper.map(participate.getClazz(), ClassDTO.class);
            classDTO.setClassId(participate.getClazz().getClassId());
            dto.setClassDTO(classDTO);

            participateClassDTOs.add(dto);
        }

        HealthCheckProgramDTO programDTO = modelMapper.map(existingProgram, HealthCheckProgramDTO.class);
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());
        programDTO.setParticipateClasses(participateClassDTOs);

        return programDTO;
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

        healthCheckProgramEntity.setStatus(newStatus);
        healthCheckProgramRepository.save(healthCheckProgramEntity);

        UserEntity admin = healthCheckProgramEntity.getAdmin();
        UserDTO adminDTO = new UserDTO();
        adminDTO.setId(admin.getUserId());
        adminDTO.setFullName(admin.getFullName());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setRole(admin.getRole());

        UserEntity nurse = healthCheckProgramEntity.getNurse();
        UserDTO nurseDTO = new UserDTO();
        nurseDTO.setId(nurse.getUserId());
        nurseDTO.setFullName(nurse.getFullName());
        nurseDTO.setEmail(nurse.getEmail());
        nurseDTO.setPhone(nurse.getPhone());
        nurseDTO.setAddress(nurse.getAddress());
        nurseDTO.setRole(nurse.getRole());

        List<ParticipateClassEntity> participateEntities = participateClassRepository.findAllByProgramId(healthCheckProgramEntity.getId());

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();
        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(healthCheckProgramEntity.getId());
            participateDTO.setClass_id(participate.getClazz().getClassId());
            participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);
            participateClassDTOs.add(participateDTO);
        }

        HealthCheckProgramDTO programDTO = new HealthCheckProgramDTO();
        programDTO.setId(healthCheckProgramEntity.getId());
        programDTO.setHealthCheckName(healthCheckProgramEntity.getHealthCheckName());
        programDTO.setDescription(healthCheckProgramEntity.getDescription());
        programDTO.setDateSendForm(healthCheckProgramEntity.getDateSendForm());
        programDTO.setStartDate(healthCheckProgramEntity.getStartDate());
        programDTO.setStatus(healthCheckProgramEntity.getStatus().toString());
        programDTO.setLocation(healthCheckProgramEntity.getLocation());

        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setParticipateClasses(participateClassDTOs);

        return programDTO;
    }

    public void createHealthCheckForm(int programId) {
        HealthCheckProgramEntity programEntity = healthCheckProgramRepository.findById(programId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình"));

        String nurseEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity nurse = userRepository.findUserByEmail(nurseEmail).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với Id: " + nurseEmail));

        List<ParticipateClassEntity> participateClasses = participateClassRepository.findAllByProgramId(programId);

        for (ParticipateClassEntity pc : participateClasses) {
            List<StudentEntity> students = studentRepository.findByClassEntity(pc.getClazz());

            for (StudentEntity student : students) {
                UserEntity parent = student.getParent();
                if (parent == null) continue;

                boolean hasUncommittedForm = healthCheckFormRepository.findHealthCheckFormEntityByHealthCheckProgramAndStudent(programEntity, student).stream().anyMatch(form -> form.getCommit() == null);

                if (hasUncommittedForm) continue;

                HealthCheckFormEntity form = new HealthCheckFormEntity();
                form.setStudent(student);
                form.setParent(parent);
                form.setNotes(null);
                form.setCommit(null);
                form.setExpDate(programEntity.getStartDate().minusDays(7));
                form.setHealthCheckProgram(programEntity);
                form.setNurse(nurse);

                healthCheckFormRepository.save(form);
            }
        }
    }

    public List<HealthCheckProgramDTO> getAllHealthCheckProgram(int adminId) {
        List<HealthCheckProgramEntity> healthCheckProgramEntityList = healthCheckProgramRepository.findAll();

        if (healthCheckProgramEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        List<HealthCheckProgramDTO> healthCheckProgramDTOList = healthCheckProgramEntityList.stream().map(entity -> {
            HealthCheckProgramDTO dto = new HealthCheckProgramDTO();

            dto.setId(entity.getId());
            dto.setHealthCheckName(entity.getHealthCheckName());
            dto.setDescription(entity.getDescription());
            dto.setDateSendForm(entity.getDateSendForm());
            dto.setStartDate(entity.getStartDate());
            dto.setStatus(entity.getStatus().name());
            dto.setLocation(entity.getLocation());

            dto.setAdminId(entity.getAdmin().getUserId());
            UserDTO adminDTO = modelMapper.map(entity.getAdmin(), UserDTO.class);
            dto.setAdminDTO(adminDTO);

            if (entity.getNurse() != null) {
                dto.setNurseId(entity.getNurse().getUserId());
                UserDTO nurseDTO = modelMapper.map(entity.getNurse(), UserDTO.class);
                dto.setNurseDTO(nurseDTO);
            }

            List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_Id(entity.getId());
            List<HealthCheckFormDTO> formDTOs = formEntities.stream().map(form -> modelMapper.map(form, HealthCheckFormDTO.class)).collect(Collectors.toList());
            dto.setHealthCheckFormDTOs(formDTOs);

            List<ParticipateClassEntity> participateEntities = participateClassRepository.findAllByProgramId(entity.getId());

            List<ParticipateClassDTO> participateClassDTOs = participateEntities.stream().map(participate -> {
                ParticipateClassDTO participateDTO = new ParticipateClassDTO();
                participateDTO.setParticipate_id(participate.getParticipateId());
                participateDTO.setProgram_id(entity.getId());
                participateDTO.setClass_id(participate.getClazz().getClassId());
                participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

                ClassEntity clazz = participate.getClazz();
                ClassDTO classDTO = new ClassDTO();
                classDTO.setClassId(clazz.getClassId());
                classDTO.setClassName(clazz.getClassName());
                classDTO.setTeacherName(clazz.getTeacherName());
                classDTO.setQuantity(clazz.getQuantity());

                participateDTO.setClassDTO(classDTO);
                return participateDTO;
            }).collect(Collectors.toList());

            dto.setParticipateClasses(participateClassDTOs);

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

        dto.setAdminId(entity.getAdmin().getUserId());
        dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);
        dto.setStatus(entity.getStatus().name());

        dto.setAdminDTO(modelMapper.map(entity.getAdmin(), UserDTO.class));

        if (entity.getNurse() != null) {
            dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
        }

        List<HealthCheckFormEntity> formEntities = healthCheckFormRepository.findAllByHealthCheckProgram_Id(id);
        List<HealthCheckFormDTO> formDTOs = formEntities.stream().map(form -> modelMapper.map(form, HealthCheckFormDTO.class)).collect(Collectors.toList());
        dto.setHealthCheckFormDTOs(formDTOs);

        List<ParticipateClassEntity> participateEntities = participateClassRepository.findAllByProgramId(id);

        List<ParticipateClassDTO> participateDTOs = new ArrayList<>();

        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(id);
            participateDTO.setClass_id(participate.getClazz().getClassId());
            participate.setType(ParticipateClassEntity.Type.HEALTH_CHECK);

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);

            participateDTOs.add(participateDTO);
        }

        dto.setParticipateClasses(participateDTOs);

        return dto;
    }

    public void deleteHealthCheckProgram(int id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findById(id);

        if (healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình kiểm tra sức khỏe");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if (healthCheckProgramEntity.getStatus() != HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể xoá chương trình khi ở trạng thái 'CHƯA BẮT ĐẦU'");
        }

        healthCheckProgramRepository.delete(healthCheckProgramEntity);
    }

    public VaccineProgramDTO createVaccineProgram(VaccineProgramRequest request, int adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin không tồn tại"));
        logger.info("NurseId: " + request.getNurseId());
        UserEntity nurse = userRepository.findById(request.getNurseId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Y tá"));

        logger.info("programdate: " + request.getStartDate());
        logger.info("formdate: " + request.getDateSendForm());

        if (request.getStartDate() == null || request.getDateSendForm() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu và ngày gửi thông báo không được để trống");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải là hôm nay hoặc trong tương lai");
        }

        if (request.getStartDate().isBefore(request.getDateSendForm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày gửi thông báo phải sớm hơn ngày chương trình được diễn ra!");
        }
        VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(request.getVaccineNameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy vaccine!"));

        Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository
                .findByVaccineNameAndStatus(vaccineNameEntity, VaccineProgramEntity.VaccineProgramStatus.NOT_STARTED);

        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình tiêm chủng '"
                    + vaccineNameEntity.getVaccineName() + "' đã tòn tại và chưa được bắt đầu!.");
        }

        VaccineProgramEntity entity = new VaccineProgramEntity();
        entity.setVaccineName(vaccineNameEntity);
        entity.setVaccineProgramName(request.getVaccineProgramName());
        entity.setDescription(request.getDescription());
        entity.setStartDate(request.getStartDate());
        entity.setDateSendForm(request.getDateSendForm());
        entity.setLocation(request.getLocation());
        entity.setUnit(request.getUnit());
        entity.setStatus(VaccineProgramEntity.VaccineProgramStatus.NOT_STARTED);
        entity.setAdmin(admin);
        entity.setNurse(nurse);
        vaccineProgramRepository.save(entity);

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();

        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy lớp với ID: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setProgramId(entity.getVaccineId());
                participate.setType(ParticipateClassEntity.Type.VACCINE);

                ParticipateClassEntity saved = participateClassRepository.save(participate);

                ParticipateClassDTO participateDTO = modelMapper.map(saved, ParticipateClassDTO.class);
                participateDTO.setClass_id(clazz.getClassId());
                participateDTO.setProgram_id(entity.getVaccineId());
                participateDTO.setType(saved.getType().toString());

                ClassDTO classDTO = modelMapper.map(clazz, ClassDTO.class);
                classDTO.setStudents(null);
                participateDTO.setClassDTO(classDTO);

                participateClassDTOs.add(participateDTO);
            }
        }

        VaccineNameDTO vaccineNameDTO = modelMapper.map(vaccineNameEntity, VaccineNameDTO.class);

        VaccineProgramDTO dto = modelMapper.map(entity, VaccineProgramDTO.class);
        dto.setNurseDTO(modelMapper.map(nurse, UserDTO.class));
        dto.setAdminDTO(modelMapper.map(admin, UserDTO.class));
        dto.setVaccineNameDTO(vaccineNameDTO);

        dto.setParticipateClassDTOs(participateClassDTOs);
        return dto;
    }

    public VaccineProgramDTO updateVaccineProgram(VaccineProgramRequest request, int id, int adminId) {
        VaccineProgramEntity existingProgram = vaccineProgramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình tiêm chủng!"));

        if (existingProgram.getStatus() == VaccineProgramEntity.VaccineProgramStatus.COMPLETED
                || existingProgram.getStatus() == VaccineProgramEntity.VaccineProgramStatus.ON_GOING
                || existingProgram.getStatus() == VaccineProgramEntity.VaccineProgramStatus.FORM_SENT
                || existingProgram.getStatus() == VaccineProgramEntity.VaccineProgramStatus.GENERATED_RESULT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể cập nhật chương trình đã hoàn thành hoặc đang diễn ra!");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ngày bắt đầu phải là ngày trong tương lai!");
        }

        if (request.getStartDate().isBefore(request.getDateSendForm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày gửi thông báo phải sớm hơn ngày chương trình được diễn ra!");
        }


        VaccineNameEntity vaccineNameEntity = vaccineNameRepository.findById(request.getVaccineNameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy vaccine!"));

        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy admin!"));

        UserEntity nurse = userRepository.findById(request.getNurseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy y tá!"));

        existingProgram.setVaccineName(vaccineNameEntity);
        existingProgram.setVaccineProgramName(request.getVaccineProgramName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setStartDate(request.getStartDate());
        existingProgram.setDateSendForm(request.getDateSendForm());
        existingProgram.setLocation(request.getLocation());
        existingProgram.setUnit(request.getUnit());
        existingProgram.setAdmin(admin);
        existingProgram.setNurse(nurse);

        vaccineProgramRepository.save(existingProgram);

        List<ParticipateClassEntity> oldParticipations = participateClassRepository
                .findAllByProgramId(existingProgram.getVaccineId());
        participateClassRepository.deleteAll(oldParticipations);

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();
        if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
            for (Integer classId : request.getClassIds()) {
                ClassEntity clazz = classRepository.findById(classId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy lớp với id: " + classId));

                ParticipateClassEntity participate = new ParticipateClassEntity();
                participate.setClazz(clazz);
                participate.setType(ParticipateClassEntity.Type.VACCINE);
                participate.setProgramId(existingProgram.getVaccineId());
                participateClassRepository.save(participate);

                ParticipateClassDTO participateDTO = new ParticipateClassDTO();
                participateDTO.setProgram_id(existingProgram.getVaccineId());
                participateDTO.setClass_id(clazz.getClassId());

                ClassDTO classDTO = modelMapper.map(clazz, ClassDTO.class);
                classDTO.setStudents(null);
                participateDTO.setClassDTO(classDTO);

                participateClassDTOs.add(participateDTO);
            }
        }

        VaccineNameDTO vaccineNameDTO = modelMapper.map(vaccineNameEntity, VaccineNameDTO.class);
        UserDTO adminDTO = modelMapper.map(admin, UserDTO.class);
        UserDTO nurseDTO = modelMapper.map(nurse, UserDTO.class);
        VaccineProgramDTO dto = modelMapper.map(existingProgram, VaccineProgramDTO.class);
        dto.setAdminId(admin.getUserId());
        dto.setAdminDTO(adminDTO);
        dto.setNurseId(nurse.getUserId());
        dto.setNurseDTO(nurseDTO);
        dto.setVaccineId(vaccineNameEntity.getVaccineNameId());
        dto.setVaccineNameDTO(vaccineNameDTO);
        dto.setParticipateClassDTOs(participateClassDTOs);
        return dto;
    }

    public VaccineProgramDTO updateVaccineProgramStatus(int id, String status) {
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy chương trình tiêm chủng"));

        VaccineProgramEntity.VaccineProgramStatus newStatus;
        try {
            newStatus = VaccineProgramEntity.VaccineProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        }

        if (vaccineProgramEntity.getStatus() == VaccineProgramEntity.VaccineProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã hoàn thành và không thể cập nhật");
        }

        if (vaccineProgramEntity.getStatus() == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã ở trạng thái: " + newStatus);
        }

        vaccineProgramEntity.setStatus(newStatus);
        vaccineProgramRepository.save(vaccineProgramEntity);

        UserEntity admin = vaccineProgramEntity.getAdmin();
        UserDTO adminDTO = new UserDTO();
        adminDTO.setId(admin.getUserId());
        adminDTO.setFullName(admin.getFullName());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setRole(admin.getRole());

        UserEntity nurse = vaccineProgramEntity.getNurse();
        UserDTO nurseDTO = new UserDTO();
        nurseDTO.setId(nurse.getUserId());
        nurseDTO.setFullName(nurse.getFullName());
        nurseDTO.setEmail(nurse.getEmail());
        nurseDTO.setPhone(nurse.getPhone());
        nurseDTO.setAddress(nurse.getAddress());
        nurseDTO.setRole(nurse.getRole());

        List<ParticipateClassEntity> participateEntities = participateClassRepository
                .findAllByProgramIdAndType(vaccineProgramEntity.getVaccineId(), ParticipateClassEntity.Type.VACCINE);

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();
        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(participate.getProgramId());
            participateDTO.setClass_id(participate.getClazz().getClassId());

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);
            participateClassDTOs.add(participateDTO);
        }

        VaccineProgramDTO programDTO = new VaccineProgramDTO();
        programDTO.setVaccineProgramId(vaccineProgramEntity.getVaccineId());
        programDTO.setVaccineProgramName(vaccineProgramEntity.getVaccineProgramName());
        programDTO.setDescription(vaccineProgramEntity.getDescription());
        programDTO.setDateSendForm(vaccineProgramEntity.getDateSendForm());
        programDTO.setStartDate(vaccineProgramEntity.getStartDate());
        programDTO.setLocation(vaccineProgramEntity.getLocation());
        programDTO.setStatus(vaccineProgramEntity.getStatus().toString());
        programDTO.setUnit(vaccineProgramEntity.getUnit());

        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setParticipateClassDTOs(participateClassDTOs);

        return programDTO;
    }

    public VaccineProgramDTO completeVaccineProgramStatus(int id, String status) {
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy chương trình tiêm chủng"));

        VaccineProgramEntity.VaccineProgramStatus newStatus;
        try {
            newStatus = VaccineProgramEntity.VaccineProgramStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
        }

        if (vaccineProgramEntity.getStatus() == VaccineProgramEntity.VaccineProgramStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã hoàn thành và không thể cập nhật");
        }

        if (vaccineProgramEntity.getStatus() == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chương trình đã ở trạng thái: " + newStatus);
        }

        //---------------------------------------------------------------------------------------------
        // Tìm xem ai không tham gia tiêm chủng
        //Tìm form đã gửi
        List<VaccineFormEntity> vaccineForms = vaccineFormRepository
                .findByVaccineProgram(vaccineProgramEntity);

        if (vaccineForms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình tiêm chủng chưa có form nào được gửi!");
        }

        for (VaccineFormEntity form : vaccineForms) {

            VaccineResultEntity vaccineResult = vaccineResultRepository
                    .findByVaccineFormEntity(form)
                    .orElse(null);

            if (vaccineResult == null) {
                VaccineResultEntity vaccineResultEntity = new VaccineResultEntity();
                vaccineResultEntity.setVaccineFormEntity(form);
                vaccineResultEntity.setStudentEntity(form.getStudent());
                vaccineResultEntity.setIsInjected(false);
                vaccineResultEntity.setResultNote("Không tham gia tiêm chủng");

                vaccineResultRepository.save(vaccineResultEntity);
            }
        }
        //---------------------------------------------------------------------------------------------

        vaccineProgramEntity.setStatus(newStatus);
        vaccineProgramRepository.save(vaccineProgramEntity);

        UserEntity admin = vaccineProgramEntity.getAdmin();
        UserDTO adminDTO = new UserDTO();
        adminDTO.setId(admin.getUserId());
        adminDTO.setFullName(admin.getFullName());
        adminDTO.setEmail(admin.getEmail());
        adminDTO.setPhone(admin.getPhone());
        adminDTO.setAddress(admin.getAddress());
        adminDTO.setRole(admin.getRole());

        UserEntity nurse = vaccineProgramEntity.getNurse();
        UserDTO nurseDTO = new UserDTO();
        nurseDTO.setId(nurse.getUserId());
        nurseDTO.setFullName(nurse.getFullName());
        nurseDTO.setEmail(nurse.getEmail());
        nurseDTO.setPhone(nurse.getPhone());
        nurseDTO.setAddress(nurse.getAddress());
        nurseDTO.setRole(nurse.getRole());

        List<ParticipateClassEntity> participateEntities = participateClassRepository
                .findAllByProgramIdAndType(vaccineProgramEntity.getVaccineId(), ParticipateClassEntity.Type.VACCINE);

        List<ParticipateClassDTO> participateClassDTOs = new ArrayList<>();
        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(participate.getProgramId());
            participateDTO.setClass_id(participate.getClazz().getClassId());

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);
            participateClassDTOs.add(participateDTO);
        }

        VaccineProgramDTO programDTO = new VaccineProgramDTO();
        programDTO.setVaccineProgramId(vaccineProgramEntity.getVaccineId());
        programDTO.setVaccineProgramName(vaccineProgramEntity.getVaccineProgramName());
        programDTO.setDescription(vaccineProgramEntity.getDescription());
        programDTO.setDateSendForm(vaccineProgramEntity.getDateSendForm());
        programDTO.setStartDate(vaccineProgramEntity.getStartDate());
        programDTO.setLocation(vaccineProgramEntity.getLocation());
        programDTO.setStatus(vaccineProgramEntity.getStatus().toString());
        programDTO.setUnit(vaccineProgramEntity.getUnit());

        programDTO.setAdminId(admin.getUserId());
        programDTO.setNurseId(nurse.getUserId());
        programDTO.setAdminDTO(adminDTO);
        programDTO.setNurseDTO(nurseDTO);
        programDTO.setParticipateClassDTOs(participateClassDTOs);

        return programDTO;
    }


    public void createVaccineForm(int programId) {
        VaccineProgramEntity programEntity = vaccineProgramRepository.findById(programId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình"));

        if (programEntity.getVaccineId() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chương trình vaccine không hợp lệ.");
        }

        String nurseEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity nurse = userRepository.findUserByEmail(nurseEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy y tá với email: " + nurseEmail));

        List<ParticipateClassEntity> participateClasses = participateClassRepository.findAllByProgramIdAndType(
                programId, ParticipateClassEntity.Type.VACCINE);

        for (ParticipateClassEntity pc : participateClasses) {
            List<StudentEntity> students = studentRepository.findByClassEntity(pc.getClazz());

            for (StudentEntity student : students) {
                UserEntity parent = student.getParent();
                if (parent == null)
                    continue;

                boolean hasUncommittedForm = vaccineFormRepository
                        .findVaccineFormEntityByVaccineProgramAndStudent(programEntity, student)
                        .stream().anyMatch(form -> form.getCommit() == null);

                if (hasUncommittedForm)
                    continue;

                VaccineFormEntity form = new VaccineFormEntity();
                form.setStudent(student);
                form.setParent(parent);
                form.setNote(null);
                form.setCommit(null);
                form.setExpDate(programEntity.getStartDate().minusDays(7));
                form.setVaccineProgram(programEntity);
                form.setNurse(nurse);

                vaccineFormRepository.save(form);
            }
        }
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

            List<VaccineUnitEntity> vaccineUnitEntities = vaccineUnitRepository.findByVaccineName_VaccineNameId(entity.getVaccineName().getVaccineNameId());
            List<VaccineUnitDTO> vaccineUnitDTOS = vaccineUnitEntities.stream().map(vaccineUnitEntity -> modelMapper.map(vaccineUnitEntity, VaccineUnitDTO.class)).collect(Collectors.toList());
            dto.getVaccineNameDTO().setVaccineUnitDTOs(vaccineUnitDTOS);

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

    public VaccineProgramDTO getVaccineProgramById(int id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findById(id);

        if (vaccineProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình tiêm chủng");
        }

        VaccineProgramEntity entity = vaccineProgramOpt.get();
        VaccineProgramDTO dto = modelMapper.map(entity, VaccineProgramDTO.class);

        dto.setAdminId(entity.getAdmin().getUserId());
        dto.setNurseId(entity.getNurse() != null ? entity.getNurse().getUserId() : null);

        dto.setAdminDTO(modelMapper.map(entity.getAdmin(), UserDTO.class));
        if (entity.getNurse() != null) {
            dto.setNurseDTO(modelMapper.map(entity.getNurse(), UserDTO.class));
        }

        dto.setStatus(entity.getStatus().name());

        List<VaccineFormEntity> formEntities = vaccineFormRepository.findByVaccineProgram_VaccineId(id);
        List<VaccineFormDTO> formDTOs = formEntities.stream()
                .map(form -> modelMapper.map(form, VaccineFormDTO.class))
                .collect(Collectors.toList());
        dto.setVaccineFormDTOs(formDTOs);

        List<ParticipateClassEntity> participateEntities = participateClassRepository
                .findAllByProgramId(id);

        List<ParticipateClassDTO> participateDTOs = new ArrayList<>();
        for (ParticipateClassEntity participate : participateEntities) {
            ParticipateClassDTO participateDTO = new ParticipateClassDTO();
            participateDTO.setParticipate_id(participate.getParticipateId());
            participateDTO.setProgram_id(id);
            participateDTO.setClass_id(participate.getClazz().getClassId());
            participate.setType(ParticipateClassEntity.Type.VACCINE);

            ClassEntity clazz = participate.getClazz();
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(clazz.getClassId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setTeacherName(clazz.getTeacherName());
            classDTO.setQuantity(clazz.getQuantity());

            participateDTO.setClassDTO(classDTO);
            participateDTOs.add(participateDTO);
        }

        dto.setParticipateClassDTOs(participateDTOs);

        return dto;
    }

    public void deleteVaccineProgram(int id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findById(id);

        if (vaccineProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình tiêm chủng");
        }

        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        VaccineProgramEntity.VaccineProgramStatus status = vaccineProgramEntity.getStatus();
        if (status == VaccineProgramEntity.VaccineProgramStatus.ON_GOING
                || status == VaccineProgramEntity.VaccineProgramStatus.COMPLETED
                || status == VaccineProgramEntity.VaccineProgramStatus.GENERATED_RESULT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể xoá chương trình đang diễn ra hoặc đã hoàn thành");
        }

        vaccineProgramRepository.delete(vaccineProgramEntity);
    }


    public List<ClassDTO> getAllClass() {
        List<ClassEntity> classEntityList = classRepository.findAll();

        if (classEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found");
        }

        return classEntityList.stream().map(classEntity -> {
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassId(classEntity.getClassId());
            classDTO.setClassName(classEntity.getClassName());
            classDTO.setTeacherName(classEntity.getTeacherName());
            classDTO.setQuantity(classEntity.getQuantity());

            if (classEntity.getStudents() != null) {
                List<StudentDTO> studentDTOList = classEntity.getStudents().stream().map(student -> {
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setStudentId(student.getId());
                    studentDTO.setFullName(student.getFullName());
                    studentDTO.setDob(student.getDob());
                    studentDTO.setGender(
                            student.getGender() != null ? student.getGender().name() : null);
                    studentDTO.setClassId(classEntity.getClassId());
                    studentDTO.setParentId(student.getParent().getUserId());

                    if (student.getParent() != null) {
                        UserDTO parentDTO = modelMapper.map(student.getParent(), UserDTO.class);
                        studentDTO.setParentDTO(parentDTO);
                    }

                    studentDTO.setClassDTO(null);

                    return studentDTO;
                }).collect(Collectors.toList());

                classDTO.setStudents(studentDTOList);
            }
            return classDTO;
        }).collect(Collectors.toList());
    }


    public List<StudentDTO> getAllStudentInClass(int classId) {
        Optional<ClassEntity> classOpt = classRepository.findById(classId);
        if (classOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học");
        }
        ClassEntity classEntity = classOpt.get();

        List<StudentEntity> studentEntityList = studentRepository.findByClassEntity(classEntity);
        if (studentEntityList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lớp này hiện không có học sinh");
        }

        return studentEntityList.stream().map(student -> {
            StudentDTO dto = modelMapper.map(student, StudentDTO.class);

            // Mapping classDTO
            // if (student.getClassEntity() != null) {
            //     ClassDTO classDTO = modelMapper.map(student.getClassEntity(), ClassDTO.class);
            //     dto.setClassDTO(classDTO);
            //     dto.setClassId(student.getClassEntity().getClassId());
            // }

            // Mapping parentDTO
            if (student.getParent() != null) {
                UserDTO parentDTO = modelMapper.map(student.getParent(), UserDTO.class);
                dto.setParentDTO(parentDTO);
                dto.setParentId(student.getParent().getUserId());
            }

            return dto;
        }).collect(Collectors.toList());
    }


    //Thien
    public RecordAndHistoryDTO getMedicalRecordByStudentId(int studentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy học sinh"));

        // Medical Record
        Optional<MedicalRecordEntity> optMedicalRecord = medicalRecordsRepository.findByStudent_Id(studentId);
        MedicalRecordDTO medicalRecordDTO;
        if (optMedicalRecord.isPresent()) {
            medicalRecordDTO = modelMapper.map(optMedicalRecord.get(), MedicalRecordDTO.class);
        } else {
            medicalRecordDTO = new MedicalRecordDTO();
            medicalRecordDTO.setStudentId(studentId);
            medicalRecordDTO.setStudentDTO(modelMapper.map(student, StudentDTO.class));
            medicalRecordDTO.setVaccineHistoryDTOS(Collections.emptyList());
        }

        // Vaccine History
        List<VaccineHistoryDTO> vaccineHistoryDTOList = vaccineHistoryRepository.findByStudent(student).stream()
                .map(history -> {
                    VaccineHistoryDTO dto = new VaccineHistoryDTO();
                    dto.setId(history.getId());
                    dto.setNote(history.getNote());
                    dto.setCreateBy(history.isCreateBy());
                    dto.setUnit(history.getUnit());
                    dto.setStudentId(student.getId());
                    dto.setStudentDTO(modelMapper.map(student, StudentDTO.class));
                    dto.setVaccineNameDTO(modelMapper.map(history.getVaccineNameEntity(), VaccineNameDTO.class));
                    return dto;
                }).collect(Collectors.toList());

        // Trả về gộp
        RecordAndHistoryDTO dto = new RecordAndHistoryDTO();
        dto.setMedicalRecord(medicalRecordDTO);
        dto.setVaccineHistories(vaccineHistoryDTOList);
        return dto;
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
                String genderStr = row.getCell(2).getStringCellValue().trim().toUpperCase();
                StudentEntity.Gender gender;
                if (genderStr.equalsIgnoreCase("nam")) {
                    gender = StudentEntity.Gender.MALE;
                } else {
                    gender = StudentEntity.Gender.FEMALE;
                }
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
        return vaccineProgramRepository.countByStatusIn((List.of(VaccineProgramEntity.VaccineProgramStatus.NOT_STARTED, VaccineProgramEntity.VaccineProgramStatus.ON_GOING)));
    }

    public long countHealthCheckProgram() {
        return healthCheckProgramRepository.countByStatusIn((List.of(HealthCheckProgramEntity.HealthCheckProgramStatus.NOT_STARTED, HealthCheckProgramEntity.HealthCheckProgramStatus.ON_GOING)));
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

    public VaccineFormStatsDTO getFormStatsByProgram(int vaccineProgramId) {
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
        if (vaccineNameRepository.findByVaccineName(request.getVaccineName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine đã tồn tại");
        }

        if (request.getVaccineUnits() == null || request.getVaccineUnits().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách mũi tiêm không được để trống");
        }

        for (VaccineUnitRequest unitReq : request.getVaccineUnits()) {
            if (unitReq.getUnit() <= 0 || unitReq.getSchedule() == null || unitReq.getSchedule().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thông tin mũi tiêm không hợp lệ");
            }
        }

        UserEntity admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Admin"));

        VaccineNameEntity vaccineEntity = new VaccineNameEntity();
        vaccineEntity.setVaccineName(request.getVaccineName());
        vaccineEntity.setManufacture(request.getManufacture());
        vaccineEntity.setAgeFrom(request.getAgeFrom());
        vaccineEntity.setAgeTo(request.getAgeTo());
        vaccineEntity.setUrl(request.getUrl());
        vaccineEntity.setDescription(request.getDescription());
        vaccineEntity.setTotalUnit(request.getVaccineUnits().size());
        vaccineEntity.setUser(admin);

        VaccineNameEntity savedVaccine = vaccineNameRepository.save(vaccineEntity);

        List<VaccineUnitEntity> unitEntities = new ArrayList<>();
        for (VaccineUnitRequest unitReq : request.getVaccineUnits()) {
            VaccineUnitEntity unit = new VaccineUnitEntity();
            unit.setVaccineName(savedVaccine);
            unit.setUnit(unitReq.getUnit());
            unit.setSchedule(unitReq.getSchedule());
            unitEntities.add(unit);
        }

        vaccineUnitRepository.saveAll(unitEntities);

        VaccineNameDTO dto = new VaccineNameDTO();
        dto.setId(savedVaccine.getVaccineNameId());
        dto.setUserId(admin.getUserId());
        dto.setVaccineName(savedVaccine.getVaccineName());
        dto.setManufacture(savedVaccine.getManufacture());
        dto.setAgeFrom(savedVaccine.getAgeFrom());
        dto.setAgeTo(savedVaccine.getAgeTo());
        dto.setTotalUnit(savedVaccine.getTotalUnit());
        dto.setUrl(savedVaccine.getUrl());
        dto.setDescription(savedVaccine.getDescription());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(admin.getUserId());
        userDTO.setFullName(admin.getFullName());
        dto.setUserDTO(userDTO);

        List<VaccineUnitDTO> unitDTOs = unitEntities.stream().map(unit -> {
            VaccineUnitDTO dtoUnit = new VaccineUnitDTO();
            dtoUnit.setUnit(unit.getUnit());
            dtoUnit.setSchedule(unit.getSchedule());
            dtoUnit.setVaccineId(unit.getUnitId());
            return dtoUnit;
        }).collect(Collectors.toList());
        dto.setVaccineUnitDTOs(unitDTOs);

        return dto;
    }


    @Transactional
    public void importVaccineNameFromExcel(MultipartFile file, int adminId) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int importedCount = 0;

            UserEntity admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

            Row headerRow = sheet.getRow(0);
            if (headerRow == null || headerRow.getPhysicalNumberOfCells() < 5) {
                throw new RuntimeException("File Excel không đúng cấu trúc. Vui lòng kiểm tra lại.");
            }

            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                if (rowNum == 0) continue;

                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                try {
                    String vaccineName = row.getCell(0).getStringCellValue();
                    String manufacture = row.getCell(1).getStringCellValue();
                    int ageFrom = (int) row.getCell(2).getNumericCellValue();
                    int ageTo = (int) row.getCell(3).getNumericCellValue();
                    int totalUnit = (int) row.getCell(4).getNumericCellValue();
                    String url = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
                    String description = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;

                    boolean exists = vaccineNameRepository.existsByVaccineNameAndManufactureAndAgeFromAndAgeToAndTotalUnitAndUrlAndDescription(
                            vaccineName, manufacture, ageFrom, ageTo, totalUnit, url, description);

                    if (exists) {
                        throw new RuntimeException("Dòng " + (rowNum) + " bị trùng trong hệ thống.");
                    }

                    VaccineNameEntity vaccine = new VaccineNameEntity();
                    vaccine.setVaccineName(vaccineName);
                    vaccine.setManufacture(manufacture);
                    vaccine.setAgeFrom(ageFrom);
                    vaccine.setAgeTo(ageTo);
                    vaccine.setTotalUnit(totalUnit);
                    vaccine.setUrl(url);
                    vaccine.setDescription(description);
                    vaccine.setUser(admin);

                    vaccineNameRepository.save(vaccine);
                    importedCount++;
                } catch (Exception e) {
                    throw new RuntimeException("Import thất bại: " + e.getMessage());
                }
            }

            if (importedCount == 0) {
                throw new RuntimeException("Không có dữ liệu hợp lệ nào được import.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }


    public List<VaccineResultExportDTO> getVaccineResultsToExport(int vaccineProgramId) {
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramRepository.findById(vaccineProgramId).orElseThrow(() ->
                new IllegalArgumentException("Không tìm thấy chương trình tiêm chủng với ID: " + vaccineProgramId));
        if (vaccineProgramEntity.getStatus() != VaccineProgramEntity.VaccineProgramStatus.COMPLETED) {
            throw new IllegalArgumentException("Hãy hoàn thành chương trình tiêm chủng '" + vaccineProgramEntity.getVaccineProgramName() + "' trước khi xuất báo cáo thống kê của chương trình!");
        }
        List<VaccineResultExportDTO> dtoList = vaccineResultRepository.findExportByProgramId(vaccineProgramId);
        logger.info("dto list size: " + dtoList.size());
        if (dtoList.isEmpty()) {
            throw new IllegalArgumentException("Không có dữ liệu để xuất Excel");
        }
        return dtoList;
    }

    public ByteArrayInputStream exportVaccineResultToExcel(int vaccineProgramId) throws IOException {
        List<VaccineResultExportDTO> dtoList = getVaccineResultsToExport(vaccineProgramId);
        ByteArrayOutputStream out = null;
        VaccineResultExportDTO first = dtoList.getFirst();

        Workbook workbook = new XSSFWorkbook();
        CellStyle titleStyle = ExcelExportStyleUtil.createTitleStyle(workbook);
        CellStyle headerStyle = ExcelExportStyleUtil.createHeaderStyle(workbook);
        CellStyle dataStyle = ExcelExportStyleUtil.createDataStyle(workbook);
        CellStyle labelStyle = ExcelExportStyleUtil.createLabelStyle(workbook);
        CellStyle sectionLabelStyle = ExcelExportStyleUtil.createSectionLabelStyle(workbook);
        Sheet sheet = workbook.createSheet("Kết quả tiêm chủng");

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO KẾT QUẢ CHƯƠNG TRÌNH TIÊM CHỦNG");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

        // Row chứa tiêu đề section
        Row infoLabelRow = sheet.createRow(3);

        // Thông tin chương trình
        Cell programInfoCell = infoLabelRow.createCell(0);
        programInfoCell.setCellValue("Thông tin chương trình");
        programInfoCell.setCellStyle(sectionLabelStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));

        // Áp style cho tất cả các ô trong vùng gộp (ô 0 và 1)
        for (int col = 0; col <= 1; col++) {
            Cell cell = infoLabelRow.getCell(col);
            if (cell == null) cell = infoLabelRow.createCell(col);
            cell.setCellStyle(sectionLabelStyle);
        }

        String[] labels1 = {"Tên chương trình", "Trạng thái", "Ngày thực hiện", "Địa điểm", "Y tá phụ trách", "Người tạo chương trình", "Mô tả chương trình", "Tổng học sinh tham gia", "Tổng học sinh không tham gia"};
        String programStatus = first.getStatus() == VaccineProgramEntity.VaccineProgramStatus.COMPLETED ? "ĐÃ HOÀN THÀNH" : "";
        Object[] values1 = {first.getProgramName(), programStatus, first.getStartDate(), first.getLocation(), first.getNurseName(), first.getAdminName(), first.getDescription(), first.getTotalNumberStudentVaccinated(), first.getTotalNumberStudentNotVaccinated()};
        for (int i = 0; i < labels1.length; i++) {
            Row row = sheet.createRow(4 + i);
            Cell labelCell = row.createCell(0);
            Cell valueCell = row.createCell(1);
            labelCell.setCellValue(labels1[i]);
            valueCell.setCellValue(String.valueOf(values1[i]));
            labelCell.setCellStyle(labelStyle);
            valueCell.setCellStyle(dataStyle);
        }

        // Thông tin vaccine
        Cell vaccineInfoCell = infoLabelRow.createCell(3);
        vaccineInfoCell.setCellValue("Thông tin vaccine");
        vaccineInfoCell.setCellStyle(sectionLabelStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 3, 4));

        // Áp style cho tất cả các ô trong vùng gộp (ô 3 và 4)
        for (int col = 3; col <= 4; col++) {
            Cell cell = infoLabelRow.getCell(col);
            if (cell == null) cell = infoLabelRow.createCell(col);
            cell.setCellStyle(sectionLabelStyle);
        }

        String[] labels2 = {"Tên vaccine", "Mũi thứ", "Nguồn gốc", "Tuổi từ", "Tuổi đến", "Mô tả vaccine"};
        Object[] values2 = {first.getVaccineName(), first.getUnit(), first.getManufature(), first.getAgeFrom(), first.getAgeTo(), first.getVaccineDescription()};
        for (int i = 0; i < labels2.length; i++) {
            Row row = sheet.getRow(4 + i);
            if (row == null) row = sheet.createRow(4 + i);
            Cell labelCell = row.createCell(3);
            Cell valueCell = row.createCell(4);
            labelCell.setCellValue(labels2[i]);
            valueCell.setCellValue(String.valueOf(values2[i]));
            labelCell.setCellStyle(labelStyle);
            valueCell.setCellStyle(dataStyle);
        }

        String[] headers = {"Mã học sinh", "Tên học sinh", "Ngày sinh", "Giới tính", "Lớp", "Phụ huynh", "SĐT", "Địa chỉ", "Quan hệ", "Phản ứng", "Xử lý", "Ghi chú", "Đã tiêm"};
        Row headerRow = sheet.createRow(14);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 15;
        for (VaccineResultExportDTO dto : dtoList) {
            Row row = sheet.createRow(rowNum++);

            String[] values = {String.valueOf(dto.getStudentId()), dto.getStudentName(), String.valueOf(dto.getDob()), String.valueOf(dto.getGender()) != null && String.valueOf(dto.getGender()).equals("MALE") ? "Nam" : "Nữ", dto.getClassName(), dto.getParentName(), dto.getPhone(), dto.getAddress(), dto.getRelationship(), dto.getReaction(), dto.getActionsTaken(), dto.getNote(), dto.getIsInjected() != null && dto.getIsInjected() ? "Đã tiêm" : "Chưa tiêm"};
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(dataStyle);
            }
        }

        out = new ByteArrayOutputStream();
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public List<HealthCheckResultExportDTO> getHealthCheckResultsToExport(int healthCheckProgramId) {
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramRepository.findById(healthCheckProgramId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chương trình khám định kỳ với ID: " + healthCheckProgramId));
        if (healthCheckProgramEntity.getStatus() != HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED) {
            throw new IllegalArgumentException("Hãy hoàn thành chương trình khám định kỳ '" + healthCheckProgramEntity.getHealthCheckName() + "' trước khi xuất báo cáo thống kê của chương trình!");
        }
        List<HealthCheckResultExportDTO> dtoList = healthCheckResultRepository.findExportByProgramId(healthCheckProgramId);
        if (dtoList.isEmpty()) {
            throw new IllegalArgumentException("Không có dữ liệu để xuất Excel");
        }
        return dtoList;
    }

    public ByteArrayInputStream exportHealthCheckResultToExcel(int healthCheckProgramId) throws IOException {
        List<HealthCheckResultExportDTO> dtoList = getHealthCheckResultsToExport(healthCheckProgramId);
        ByteArrayOutputStream out = null;
        HealthCheckResultExportDTO first = dtoList.getFirst();

        Workbook workbook = new XSSFWorkbook();
        CellStyle titleStyle = ExcelExportStyleUtil.createTitleStyle(workbook);
        CellStyle headerStyle = ExcelExportStyleUtil.createHeaderStyle(workbook);
        CellStyle dataStyle = ExcelExportStyleUtil.createDataStyle(workbook);
        CellStyle labelStyle = ExcelExportStyleUtil.createLabelStyle(workbook);
        CellStyle sectionLabelStyle = ExcelExportStyleUtil.createSectionLabelStyle(workbook);
        Sheet sheet = workbook.createSheet("Kết quả khám định kỳ");

        Row titleRow = sheet.createRow(1);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO KẾT QUẢ CHƯƠNG TRÌNH KHÁM ĐỊNH KỲ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

        // Row chứa tiêu đề section
        Row infoLabelRow = sheet.createRow(3);

        // Thông tin chương trình
        Cell programInfoCell = infoLabelRow.createCell(0);
        programInfoCell.setCellValue("Thông tin chương trình");
        programInfoCell.setCellStyle(sectionLabelStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));

        // Áp style cho tất cả các ô trong vùng gộp (ô 0 và 1)
        for (int col = 0; col <= 1; col++) {
            Cell cell = infoLabelRow.getCell(col);
            if (cell == null) cell = infoLabelRow.createCell(col);
            cell.setCellStyle(sectionLabelStyle);
        }


        String[] labels1 = {"Tên chương trình", "Trạng thái", "Ngày thực hiện", "Địa điểm", "Y tá phụ trách", "Người tạo chương trình", "Mô tả chương trình", "Tổng học sinh tham gia", "Tổng học sinh không tham gia"};
        String programStatus = first.getStatus() == HealthCheckProgramEntity.HealthCheckProgramStatus.COMPLETED ? "ĐÃ HOÀN THÀNH" : "";
        Object[] values1 = {first.getProgramName(), programStatus, first.getStartDate(), first.getLocation(), first.getNurseName(), first.getAdminName(), first.getDescription(), first.getTotalNumberStudentChecked(), first.getTotalNumberStudentNotChecked()};
        for (int i = 0; i < labels1.length; i++) {
            Row row = sheet.createRow(4 + i);
            Cell labelCell = row.createCell(0);
            Cell valueCell = row.createCell(1);
            labelCell.setCellValue(labels1[i]);
            valueCell.setCellValue(String.valueOf(values1[i]));
            labelCell.setCellStyle(labelStyle);
            valueCell.setCellStyle(dataStyle);
        }

        String[] headers = {"Mã học sinh", "Tên học sinh", "Ngày sinh", "Giới tính", "Lớp", "Phụ huynh", "SĐT", "Địa chỉ", "Quan hệ", "Tổng quan", "Thị giác", "Thính giác", "Cân nặng (kg)", "Chiều cao (cm)", "Răng miệng", "Huyết áp", "Nhịp tim", "Ghi chú", "Đã khám"};
        Row headerRow = sheet.createRow(14);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 15;
        for (HealthCheckResultExportDTO dto : dtoList) {
            Row row = sheet.createRow(rowNum++);

            String[] values = {String.valueOf(dto.getStudentId()), dto.getStudentName(), String.valueOf(dto.getDob()), String.valueOf(dto.getGender()) != null && String.valueOf(dto.getGender()).equals("MALE") ? "Nam" : "Nữ", dto.getClassName(), dto.getParentName(), dto.getPhone(), dto.getAddress(), dto.getRelationship(), dto.getGeneralCondition(), dto.getVision(), dto.getHearing(), dto.getWeight() != null ? dto.getWeight().toString() : "", dto.getHeight() != null ? dto.getHeight().toString() : "", dto.getDentalStatus(), dto.getBloodPressure(), dto.getHeartRate() != null ? dto.getHeartRate().toString() : "", dto.getNote(), dto.getIsChecked() != null && dto.getIsChecked() ? "ĐÃ KHÁM" : "CHƯA KHÁM"};
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(dataStyle);
            }
        }

        out = new ByteArrayOutputStream();
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

}
