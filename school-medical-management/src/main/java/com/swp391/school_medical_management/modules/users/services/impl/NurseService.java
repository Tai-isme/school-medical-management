package com.swp391.school_medical_management.modules.users.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormCreateRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineFormUpdateRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckFormDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.MedicalRequestDetailDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineFormDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckFormEntity;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestDetailEntity;
import com.swp391.school_medical_management.modules.users.entities.MedicalRequestEntity;
import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineFormEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.MedicalRequestRepository;
import com.swp391.school_medical_management.modules.users.repositories.StudentRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineFormRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineProgramRepository;

@Service
public class NurseService {

    @Autowired private MedicalRequestRepository medicalRequestRepository;

    @Autowired private ModelMapper modelMapper;

    @Autowired private UserRepository userRepository;

    @Autowired private StudentRepository studentRepository;

    @Autowired private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired private HealthCheckFormRepository healthCheckFormRepository;

    @Autowired private VaccineProgramRepository vaccineProgramRepository;

    @Autowired private VaccineFormRepository vaccineFormRepository;

    public List<MedicalRequestDTO> getPendingMedicalRequest() {
        String status = "PROCESSING";
        List<MedicalRequestEntity> pendingMedicalRequestList = medicalRequestRepository.findMedicalRequestEntitiesByStatusIgnoreCase(status);
        if(pendingMedicalRequestList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No processing medical requests found");
        }
        List<MedicalRequestDTO> medicalRequestDTOList = new ArrayList<>();
        for (MedicalRequestEntity medicalRequestEntity : pendingMedicalRequestList) {
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

    public MedicalRequestDTO updateMedicalRequestStatus(int requestId, String status) {
        Optional<MedicalRequestEntity> medicalRequestOpt = medicalRequestRepository.findMedicalRequestEntityByRequestId(requestId);
        if(medicalRequestOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical request not found");
        MedicalRequestEntity medicalRequest = medicalRequestOpt.get();
        if(status == null || !status.toUpperCase().equals("ACCEPTED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        medicalRequest.setStatus(status.toUpperCase());
        medicalRequestRepository.save(medicalRequest);
        return modelMapper.map(medicalRequest, MedicalRequestDTO.class);
    }

    public List<HealthCheckFormDTO> createHealthCheckForm(Long nurseId, HealthCheckFormCreateRequest request) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        }

        UserEntity nurse = nurseOpt.get();


        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = 
        healthCheckProgramRepository.findHealthCheckProgramEntityById(request.getHealthCheckProgramId());

        if(healthCheckProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");
        }

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if(healthCheckProgramEntity.getStatus().equals("ON_GOING") || healthCheckProgramEntity.getStatus().equals("COMPLETED")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program already started or completed");
        }

        List<HealthCheckFormDTO> result = new ArrayList<>();

        for(Long studentId : request.getStudentIds()){
            Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
            if(studentOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found" + studentId);
            StudentEntity student = studentOpt.get();
            
            UserEntity parent = student.getParent();
            if(parent == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found for student with ID: " + studentId);

            Optional<HealthCheckFormEntity> existingFormOpt = healthCheckFormRepository.findHealthCheckFormEntityByHealthCheckProgramAndStudent(healthCheckProgramEntity, student);
            
            if(existingFormOpt.isPresent()) {
                if(existingFormOpt.get().getCommit() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed by parent for student with ID: " + studentId);
                }
            }

            HealthCheckFormEntity healthCheckFormEntity = new HealthCheckFormEntity();
            healthCheckFormEntity.setStudent(student);
            healthCheckFormEntity.setParent(parent);
            healthCheckFormEntity.setNurse(nurse);
            // healthCheckFormEntity.setFormDate(request.getFormDate());
            healthCheckFormEntity.setFormDate(LocalDate.now());
            healthCheckFormEntity.setNotes(null);
            healthCheckFormEntity.setCommit(null);
            healthCheckFormEntity.setHealthCheckProgram(healthCheckProgramEntity);
            healthCheckFormRepository.save(healthCheckFormEntity);

            result.add(modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class));
        }
        return result;
    }

    public Map<String, Object> updateHealthCheckForm(Long nurseId, HealthCheckFormUpdateRequest request) {

        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findHealthCheckProgramEntityById(request.getHealthCheckProgramId());

        if(healthCheckProgramOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if(healthCheckProgramEntity.getStatus().equals("ON_GOING") || healthCheckProgramEntity.getStatus().equals("COMPLETED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program already started or completed");

        List<HealthCheckFormDTO> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        for(Long healthCheckFormId : request.getHealthCheckFormIds()) {
            try{
            Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findHealCheckFormEntityById(healthCheckFormId);
            if(healthCheckFormOpt.isEmpty()) 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found with ID: " + healthCheckFormId);
            
            HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();

            if(!healthCheckFormEntity.getNurse().getUserId().equals(nurseId)) 
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for health check form with ID: " + healthCheckFormId);

            if(healthCheckFormEntity.getCommit() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed by parent");

            healthCheckFormEntity.setHealthCheckProgram(healthCheckProgramEntity);
            healthCheckFormEntity.setFormDate(LocalDate.now());
            healthCheckFormRepository.save(healthCheckFormEntity);

            successList.add(modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class));

            } catch (ResponseStatusException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("formId", healthCheckFormId);
                error.put("error", e.getReason());
                errorList.add(error);
            }
            
        }
        Map<String, Object> result = new LinkedHashMap();
        result.put("errors", errorList);
        result.put("success", successList);
        return result;
    }

    public List<HealthCheckFormDTO> getAllHealthCheckForms(Long nurseId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        List<HealthCheckFormEntity> healthCheckFormEntitieList = healthCheckFormRepository.findAll();

        if (healthCheckFormEntitieList.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check forms found");
        
        List<HealthCheckFormDTO> healthCheckFormDTOList = healthCheckFormEntitieList
                .stream()
                .map(healthCheckFormEntity -> modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class))
                .collect(Collectors.toList());

        return healthCheckFormDTOList;
    }

    public HealthCheckFormDTO getHealthCheckFormById(Long nurseId, Long healthCheckFormId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findHealCheckFormEntityById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        if (!healthCheckFormEntity.getNurse().getUserId().equals(nurseId)) 
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for health check form ");
        
        return modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class);
    }

    public List<HealthCheckFormDTO> getAllCommitedTrueHealthCheckForm(Long nurseId) {
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        
        List<HealthCheckFormEntity> healthCheckFormEntitieList = healthCheckFormRepository.findByCommitTrue();

        if (healthCheckFormEntitieList.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No committed health check forms found");
        
        List<HealthCheckFormDTO> healthCheckFormDTOList = healthCheckFormEntitieList
                .stream()
                .map(healthCheckFormEntity -> modelMapper.map(healthCheckFormEntity, HealthCheckFormDTO.class))
                .collect(Collectors.toList());

        return healthCheckFormDTOList;
    }


    public void deleteHealthCheckForm(Long healthCheckFormId) {
        Optional<HealthCheckFormEntity> healthCheckFormOpt = healthCheckFormRepository.findHealCheckFormEntityById(healthCheckFormId);
        if (healthCheckFormOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check form not found");
        // if(healthCheckFormOpt.get().getCommit() != null) 
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check form already committed by parent");
        HealthCheckFormEntity healthCheckFormEntity = healthCheckFormOpt.get();
        healthCheckFormRepository.delete(healthCheckFormEntity);
    }

    public List<VaccineFormDTO> createVaccineForm(Long nurseId, VaccineFormCreateRequest request){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        }

        UserEntity nurse = nurseOpt.get();


        Optional<VaccineProgramEntity> vaccineProgramOpt = 
        vaccineProgramRepository.findVaccineProgramByVaccineId(request.getVaccineProgramId());

        if(vaccineProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        }

        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        if(vaccineProgramEntity.getStatus().equals("ON_GOING") || vaccineProgramEntity.getStatus().equals("COMPLETED")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine program already started or completed");
        }

        List<VaccineFormDTO> result = new ArrayList<>();

        for(Long studentId : request.getStudentIds()){
            Optional<StudentEntity> studentOpt = studentRepository.findStudentById(studentId);
            if(studentOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found" + studentId);

            StudentEntity student = studentOpt.get();
            
            UserEntity parent = student.getParent();
            if(parent == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent not found for student with ID: " + studentId);

            Optional<VaccineFormEntity> existingFormOpt = vaccineFormRepository.findVaccineFormEntityByVaccineProgramAndStudent(vaccineProgramEntity, student);
            
            if(existingFormOpt.isPresent()) {
                if(existingFormOpt.get().getCommit() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed by parent for student with ID: " + studentId);
                }
            }

            VaccineFormEntity vaccineFormEntity = new VaccineFormEntity();

            vaccineFormEntity.setStudent(student);
            vaccineFormEntity.setNote(null);
            vaccineFormEntity.setCommit(null);
            vaccineFormEntity.setNurse(nurse);
            vaccineFormEntity.setFormDate(LocalDate.now());
            vaccineFormEntity.setParent(parent);
            vaccineFormEntity.setVaccineProgram(vaccineProgramEntity);
            vaccineFormRepository.save(vaccineFormEntity);

            result.add(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));
        }
        return result;
    }

    public Map<String, Object> updateVaccineForm(Long nurseId, VaccineFormUpdateRequest request){
         Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(request.getVaccineProgramId());

        if(vaccineProgramOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");

        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();

        if(vaccineProgramEntity.getStatus().equals("ON_GOING") || vaccineProgramEntity.getStatus().equals("COMPLETED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine program already started or completed");

        List<VaccineFormDTO> successList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();

        for(Long vaccineFormId : request.getVaccineFormIds()) {
            try{
            Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
            if(vaccineFormOpt.isEmpty()) 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found with ID: " + vaccineFormId);
            
            VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();

            if(!vaccineFormEntity.getNurse().getUserId().equals(nurseId)) 
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for vaccine form with ID: " + vaccineFormId);

            if(vaccineFormEntity.getCommit() != null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine form already committed by parent");

            vaccineFormEntity.setVaccineProgram(vaccineProgramEntity);
            vaccineFormEntity.setFormDate(LocalDate.now());
            vaccineFormRepository.save(vaccineFormEntity);

            successList.add(modelMapper.map(vaccineFormEntity, VaccineFormDTO.class));

            } catch (ResponseStatusException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("formId", vaccineFormId);
                error.put("error", e.getReason());
                errorList.add(error);
            }
            
        }
        Map<String, Object> result = new LinkedHashMap();
        result.put("errors", errorList);
        result.put("success", successList);
        return result;
    }

    public List<VaccineFormDTO> getAllVaccinForm(Long nurseId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        List<VaccineFormEntity> vaccineFormEntitieList = vaccineFormRepository.findAll();
        if(vaccineFormEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine forms found");
        List<VaccineFormDTO> vaccineFormDTOList = vaccineFormEntitieList
                .stream()
                .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class))
                .collect(Collectors.toList());
        return vaccineFormDTOList;
    }

    public VaccineFormDTO getVaccinFormById(Long nurseId, Long vaccineFormId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        if(!vaccineFormEntity.getNurse().getUserId().equals(nurseId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for vaccine form");
        return modelMapper.map(vaccineFormEntity, VaccineFormDTO.class);
    }

    public List<VaccineFormDTO> getAllCommitedTrueVaccineForm(Long nurseId){
        Optional<UserEntity> nurseOpt = userRepository.findUserByUserId(nurseId);
        if (nurseOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse not found");
        List<VaccineFormEntity> vaccineFormEntitieList = vaccineFormRepository.findByCommitTrue();
        if(vaccineFormEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No commited vaccine forms found");
        List<VaccineFormDTO> vaccineFormDTOList = vaccineFormEntitieList
                .stream()
                .map(vaccineFormEntity -> modelMapper.map(vaccineFormEntity, VaccineFormDTO.class))
                .collect(Collectors.toList());
        return vaccineFormDTOList;
    }

    public void deleteVaccineForm(long vaccineFormId){
        Optional<VaccineFormEntity> vaccineFormOpt = vaccineFormRepository.findVaccineFormEntityByvaccineFormId(vaccineFormId);
        if(vaccineFormOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine form not found");
        VaccineFormEntity vaccineFormEntity = vaccineFormOpt.get();
        vaccineFormRepository.delete(vaccineFormEntity);
    }
}

