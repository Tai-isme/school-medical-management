package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "medical_request")
public class MedicalRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int requestId;

    @Column(name = "request_name", length = 100)
    private String requestName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "reason_rejected", length = 500)
    private String reasonRejected;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private MedicalRequestStatus status;

    public enum MedicalRequestStatus {
        CONFIRMED, COMPLETED, PROCESSING, CANCELLED
    }

    private String image;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id")
    private UserEntity nurse;

    @OneToMany(mappedBy = "medicalRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRequestDetailEntity> medicalRequestDetailEntities;
}
