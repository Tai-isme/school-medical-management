package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.StudentEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import com.swp391.school_medical_management.modules.users.entities.VaccineUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccineUnitRepository extends JpaRepository<VaccineUnitEntity, Integer> {
    List<VaccineUnitEntity> findByVaccineName_VaccineNameId(int vaccineNameId);

    List<VaccineUnitEntity> findByVaccineName(VaccineNameEntity vaccineName);
}
