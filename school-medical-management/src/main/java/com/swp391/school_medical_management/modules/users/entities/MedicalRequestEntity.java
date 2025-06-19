package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "medical_request")
public class MedicalRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "request_name", length = 100)
    private String requestName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private MedicalRequestStatus status;

    public enum MedicalRequestStatus{
        SUBMITTED, COMPLETED, PROCESSING, CANCELLED
    }

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;

    @OneToMany(mappedBy = "medicalRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRequestDetailEntity> medicalRequestDetailEntities;
}