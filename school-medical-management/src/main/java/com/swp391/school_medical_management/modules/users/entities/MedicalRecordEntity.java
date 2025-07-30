package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "medical_record")
public class MedicalRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private int recordId;

    @Column(name = "allergies", length = 255)
    private String allergies;

    @Column(name = "chronic_disease", length = 255)
    private String chronicDisease;

    @Column(name = "vision", length = 50)
    private String vision;

    @Column(name = "hearing", length = 50)
    private String hearing;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private int height;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "create_by")
    private boolean createBy;

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "medical_record_ibfk_1"))
    private StudentEntity student;
}
