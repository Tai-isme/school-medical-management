package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.VaccineHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineHistoryRepository extends JpaRepository<VaccineHistory,Long> {
}
