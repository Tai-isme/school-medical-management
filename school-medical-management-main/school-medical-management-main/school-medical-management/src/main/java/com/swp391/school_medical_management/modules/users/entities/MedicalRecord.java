package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "medical_record")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(name = "allergies")
    private String allergies;

    @Column(name = "chronic_disease")
    private String chronicDisease;

    @Column(name = "treatment_history")
    private String treatmentHistory;

    @Column(name = "vision")
    private String vision;

    @Column(name = "hearing")
    private String hearing;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "high")
    private Float high;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccineHistory> vaccineHistories;
}

