package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_history")
public class VaccineHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_history_id")
    private Long id;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MedicalRecord medicalRecord;
}

