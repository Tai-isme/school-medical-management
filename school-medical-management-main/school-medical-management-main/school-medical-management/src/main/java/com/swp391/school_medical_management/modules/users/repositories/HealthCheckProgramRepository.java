package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.HealthCheckProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckProgramRepository extends JpaRepository<HealthCheckProgram,Long> {
}
