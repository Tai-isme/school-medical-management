package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_form")
public class VaccineFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_form_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "vaccine_id", referencedColumnName = "vaccine_id")
    private VaccineProgramEntity vaccineProgram;

    @ManyToOne
    @JoinColumn(name = "vaccine_name_id", referencedColumnName = "vaccine_name_id")
    private VaccineNameEntity vaccineName;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id")
    private UserEntity nurse;

    @Column(name = "exp_date")
    private LocalDate expDate;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "commit")
    private Boolean commit;
}
