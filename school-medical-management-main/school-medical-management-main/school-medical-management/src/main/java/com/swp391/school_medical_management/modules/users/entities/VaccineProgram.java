package com.swp391.school_medical_management.modules.users.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vaccine_program")
public class VaccineProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_id")
    private Long id;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "description")
    private String description;

    @Column(name = "vaccine_date")
    private Date vaccineDate;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
}
