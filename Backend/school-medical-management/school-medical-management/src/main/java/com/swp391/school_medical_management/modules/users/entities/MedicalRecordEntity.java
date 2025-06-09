package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "medical_record")
public class MedicalRecordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private long recordId;

    private String allergies;

    @Column(name = "chronic_disease")
    private String chronicDisease;

    @Column(name = "treatment_history")
    private String treatmentHistory;

    private String vision;
    private String hearing;
    private double weight;
    private double height;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccineHistoryEntity> vaccineHistories;
}