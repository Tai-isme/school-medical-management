package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vaccine_form")
public class VaccineForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_form_id")
    private Long id;

    @Column(name = "form_date")
    private LocalDate formDate;

    @Column(name = "note")
    private String note;

    @Column(name = "commit")
    private Boolean commit;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private VaccineProgram vaccineProgram;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private User nurse;
}
