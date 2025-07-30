package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "vaccine_history")
public class VaccineHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_history_id")
    private int id;

    @Column(name = "note")
    private String note;

    @Column(name = "create_by")
    private boolean createBy;

    private int unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_name_id", referencedColumnName = "vaccine_name_id")
    private VaccineNameEntity vaccineNameEntity;
}
