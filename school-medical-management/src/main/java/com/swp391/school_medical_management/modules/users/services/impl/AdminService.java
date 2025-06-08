package com.swp391.school_medical_management.modules.users.services.impl;

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

import com.swp391.school_medical_management.modules.users.dtos.request.HealthCheckProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.request.VaccineProgramRequest;
import com.swp391.school_medical_management.modules.users.dtos.response.HealthCheckProgramDTO;
import com.swp391.school_medical_management.modules.users.dtos.response.VaccineProgramDTO;
import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgramEntity;
import com.swp391.school_medical_management.modules.users.entities.UserEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineProgramEntity;
import com.swp391.school_medical_management.modules.users.repositories.HealthCheckProgramRepository;
import com.swp391.school_medical_management.modules.users.repositories.UserRepository;
import com.swp391.school_medical_management.modules.users.repositories.VaccineProgramRepository;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);


    @Autowired private HealthCheckProgramRepository healthCheckProgramRepository;

    @Autowired private VaccineProgramRepository vaccineProgramRepository;

    @Autowired private ModelMapper modelMapper;

    @Autowired private UserRepository userRepository;
    
    public HealthCheckProgramDTO createHealthCheckProgram(HealthCheckProgramRequest request, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
                
        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository
                .findHealthCheckProgramEntityByHealthCheckNameAndStatus(request.getHealthCheckName(), "NOT_STARTED");
                
        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health check program with name '" + request.getHealthCheckName() + "' already exists and not started.");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");

        HealthCheckProgramEntity healthCheckProgramEntity = new HealthCheckProgramEntity();
        healthCheckProgramEntity.setHealthCheckName(request.getHealthCheckName());
        healthCheckProgramEntity.setDescription(request.getDescription());
        healthCheckProgramEntity.setStartDate(request.getStartDate());
        healthCheckProgramEntity.setEndDate(request.getEndDate());
        healthCheckProgramEntity.setNote(request.getNote());
        healthCheckProgramEntity.setAdmin(admin);
        healthCheckProgramEntity.setStatus("NOT_STARTED");
        healthCheckProgramRepository.save(healthCheckProgramEntity);
        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public HealthCheckProgramDTO updateHealthCheckProgram(Long id, HealthCheckProgramRequest request, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        Optional<HealthCheckProgramEntity> existingProgramOpt = healthCheckProgramRepository.findHealthCheckProgramEntityById(id);

        if(existingProgramOpt.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity existingProgram = existingProgramOpt.get();

        if (!existingProgram.getAdmin().getUserId().equals(admin.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this program");
        }

        if (existingProgram.getStatus().equals("COMPLETED") || existingProgram.getStatus().equals("ON_GOING")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot update 'COMPLETED' or 'ON_GOING' program");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");

        if (!existingProgram.getHealthCheckName().equals(request.getHealthCheckName())) {
        Optional<HealthCheckProgramEntity> duplicateProgramOpt = healthCheckProgramRepository
                .findHealthCheckProgramEntityByHealthCheckNameAndStatus(request.getHealthCheckName(), "NOT_STARTED");
                if(duplicateProgramOpt.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Health check program with name '" + request.getHealthCheckName() + "' already exists and not started.");
                }
        }

        existingProgram.setHealthCheckName(request.getHealthCheckName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setStartDate(request.getStartDate());
        existingProgram.setEndDate(request.getEndDate());
        existingProgram.setNote(request.getNote());
        
        healthCheckProgramRepository.save(existingProgram);
        return modelMapper.map(existingProgram, HealthCheckProgramDTO.class);
    }

    public List<HealthCheckProgramDTO> getAllHealthCheckProgram(long adminId) {
        // UserEntity admin = userRepository.findUserByUserId(adminId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        List<HealthCheckProgramEntity> healthCheckProgramEntityList = healthCheckProgramRepository.findAll();
        if (healthCheckProgramEntityList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No health check programs found");
            List<HealthCheckProgramDTO> healthCheckProgramDTOList = healthCheckProgramEntityList.stream()
                .map(entity -> modelMapper.map(entity, HealthCheckProgramDTO.class))
                .toList();
        return healthCheckProgramDTOList;
    }

    public HealthCheckProgramDTO getHealthCheckProgramById(long adminId, Long id) {
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findHealthCheckProgramEntityById(id);
        if(healthCheckProgramOpt.isEmpty()) 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");
        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();
        return modelMapper.map(healthCheckProgramEntity, HealthCheckProgramDTO.class);
    }

    public void deleteHealthCheckProgram(Long id, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        Optional<HealthCheckProgramEntity> healthCheckProgramOpt = healthCheckProgramRepository.findHealthCheckProgramEntityById(id);
        if(healthCheckProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Health check program not found");

        HealthCheckProgramEntity healthCheckProgramEntity = healthCheckProgramOpt.get();

        if (!healthCheckProgramEntity.getAdmin().getUserId().equals(admin.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this program");
        }

        if (healthCheckProgramEntity.getStatus().equals("COMPLETED") || healthCheckProgramEntity.getStatus().equals("ON_GOING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete 'COMPLETED' or 'ON_GOING' program");
        }

        healthCheckProgramRepository.delete(healthCheckProgramEntity);
    }

    public VaccineProgramDTO createVaccineProgram(VaccineProgramRequest request, long adminId) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
       
        Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository
                .findVaccineProgramByVaccineNameAndStatus(request.getVaccineName(), "NOT_STARTED");
                
        if (existingProgramOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vaccine program with name '" + request.getVaccineName() + "' already exists and not started.");
        }

        VaccineProgramEntity vaccineProgramEntity = new VaccineProgramEntity();
        vaccineProgramEntity.setVaccineName(request.getVaccineName());
        vaccineProgramEntity.setDescription(request.getDescription());
        vaccineProgramEntity.setVaccineDate(request.getVaccineDate());
        vaccineProgramEntity.setNote(request.getNote());
        vaccineProgramEntity.setStatus("NOT_STARTED");
        vaccineProgramEntity.setAdmin(admin);
        vaccineProgramRepository.save(vaccineProgramEntity);
        return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
    }

    public VaccineProgramDTO updateVaccineProgram(VaccineProgramRequest request, long adminId, long id) {
        UserEntity admin = userRepository.findUserByUserId(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        Optional<VaccineProgramEntity> existingProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (existingProgramOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        }
        VaccineProgramEntity existingProgram = existingProgramOpt.get();
        if (!existingProgram.getAdmin().getUserId().equals(admin.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this program");
        }

        if (existingProgram.getStatus().equals("COMPLETED") || existingProgram.getStatus().equals("ON_GOING")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Cannot update 'COMPLETED' or 'ON_GOING' program");
        }
        existingProgram.setVaccineName(request.getVaccineName());
        existingProgram.setDescription(request.getDescription());
        existingProgram.setVaccineDate(request.getVaccineDate());
        existingProgram.setNote(request.getNote());
        vaccineProgramRepository.save(existingProgram);
        return modelMapper.map(existingProgram, VaccineProgramDTO.class);
    }

    public List<VaccineProgramDTO> getAllVaccineProgram(long adminId) {
        // UserEntity admin = userRepository.findUserByUserId(adminId)
        //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        List<VaccineProgramEntity> vaccineProgramEntitieList = vaccineProgramRepository.findAll();
        if (vaccineProgramEntitieList.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No vaccine programs found");
            List<VaccineProgramDTO> vaccineProgramDTOList = vaccineProgramEntitieList.stream()
                .map(entity -> modelMapper.map(entity, VaccineProgramDTO.class))
                .toList();
        return vaccineProgramDTOList;
    }

    public VaccineProgramDTO getVaccineProgramById(long id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (vaccineProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        return modelMapper.map(vaccineProgramEntity, VaccineProgramDTO.class);
    }

    public void deleteVaccineProgram(long id) {
        Optional<VaccineProgramEntity> vaccineProgramOpt = vaccineProgramRepository.findVaccineProgramByVaccineId(id);
        if (vaccineProgramOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vaccine program not found");
        VaccineProgramEntity vaccineProgramEntity = vaccineProgramOpt.get();
        vaccineProgramRepository.delete(vaccineProgramEntity);
    }
}
