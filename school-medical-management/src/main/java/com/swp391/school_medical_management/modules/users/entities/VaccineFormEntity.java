package com.swp391.school_medical_management.modules.users.entities;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_form")
public class VaccineFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_form_id")
    private Integer vaccineFormId;

    @ManyToOne
    @JoinColumn(name = "vaccine_id", referencedColumnName = "vaccine_id")
    private VaccineProgramEntity vaccineProgram;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "user_id")
    private UserEntity nurse;

    @Column(name = "form_date")
    private LocalDate formDate;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "commit")
    private Boolean commit;
}
