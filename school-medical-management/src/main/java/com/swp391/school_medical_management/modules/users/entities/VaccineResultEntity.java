package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_result")
public class VaccineResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_result_id")
    private Integer vaccineResultId;

    @Column(name = "result_note", length = 255)
    private String resultNote;

    @Column(name = "reaction", length = 255)
    private String reaction;

    @Column(name = "actions_taken", length = 255)
    private String actionsTaken;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "is_rejected")
    private Boolean isRejected;

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
