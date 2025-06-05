package com.swp391.school_medical_management.modules.users.repositories;

import com.swp391.school_medical_management.modules.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    Optional<User> findByIdAndRole(Long id, String role);

    // Tìm theo role
    List<User> findByRoleOrderByFullNameAsc(String role);
    List<User> findByFullNameContainingIgnoreCaseAndRoleOrderByFullNameAsc(String fullName, String role);

}
