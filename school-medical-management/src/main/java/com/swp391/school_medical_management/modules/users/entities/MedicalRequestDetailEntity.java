package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_request_detail")
public class MedicalRequestDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private int detailId;

    @Column(name = "medicine_name", length = 100)
    private String medicineName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "time_schedule", length = 100)
    private String timeSchedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private Status status;

    public enum Status {
        Taken, Not_Taken
    }

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    private MedicalRequestEntity medicalRequest;
}
