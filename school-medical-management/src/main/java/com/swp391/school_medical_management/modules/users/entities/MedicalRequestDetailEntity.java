package com.swp391.school_medical_management.modules.users.entities;

import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "detail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int detailId;   
    @Column(name = "medicine_name")
    private String medicineName;
    private int quantity;
    private String instruction;
    private Time time;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    private MedicalRequestEntity medicalRequest;
}
