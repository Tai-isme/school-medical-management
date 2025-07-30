package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_name")
public class VaccineNameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_name_id")
    private int vaccineNameId;

    @Column(name = "vaccine_name", nullable = false, length = 100)
    private String vaccineName;

    @Column(name = "manufacture", length = 100)
    private String manufacture;

    @Column(name = "age_from")
    private int ageFrom;

    @Column(name = "age_to")
    private int ageTo;

    @Column(name = "total_unit")
    private int totalUnit;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;
}
