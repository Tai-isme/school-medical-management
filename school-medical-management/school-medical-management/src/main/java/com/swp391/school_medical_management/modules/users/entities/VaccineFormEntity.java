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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vaccine_id", referencedColumnName = "vaccine_id")
    private VaccineProgramEntity vaccineProgram;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "user_id")
    private UserEntity parent;

    @Column(name = "form_date")
    private LocalDate formDate;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "commit")
    private Boolean commit;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VaccineFormStatus status;

    public enum VaccineFormStatus {
        DRAFT, SENT
    }
}
