package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.VaccineNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineNameRepository extends JpaRepository<VaccineNameEntity, Integer> {
    boolean existsByVaccineNameAndManufactureAndAgeFromAndAgeToAndTotalUnitAndUrlAndDescription(String vaccineName, String manufacture, int ageFrom, int ageTo, int totalUnit, String url, String description);
    // Optional<VaccineNameEntity> findByVaccineName(String vaccineName); 
    // Optional<VaccineNameEntity> findByVaccineNameAndManufactureAndUrlAndNote (String name, String url,String manufacture, String note);
}
