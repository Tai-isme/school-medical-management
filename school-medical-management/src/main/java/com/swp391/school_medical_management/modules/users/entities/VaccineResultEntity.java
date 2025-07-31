package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_result")
public class VaccineResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_result_id")
    private int vaccineResultId;

    @Column(name = "result_note", length = 255)
    private String resultNote;

    @Column(name = "reaction", length = 255)
    private String reaction;

    @Column(name = "actions_taken", length = 255)
    private String actionsTaken;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "is_injected")
    private Boolean isInjected;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id")
    private UserEntity nurseEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity studentEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_form_id", referencedColumnName = "vaccine_form_id")
    private VaccineFormEntity vaccineFormEntity;
}
