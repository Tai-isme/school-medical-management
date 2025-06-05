package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.VaccineProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineProgramRepository extends JpaRepository<VaccineProgram,Long> {
}
