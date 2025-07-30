package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_unit")
public class VaccineUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private int unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_name_id", referencedColumnName = "vaccine_name_id", nullable = false)
    private VaccineNameEntity vaccineName;

    @Column(name = "unit", nullable = false)
    private Integer unit;

    @Column(name = "schedule", length = 255)
    private String schedule;
}
