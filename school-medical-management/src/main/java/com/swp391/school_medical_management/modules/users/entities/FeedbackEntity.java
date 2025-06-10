package com.swp391.school_medical_management.modules.users.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback")
@Builder
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @Column(name = "satisfaction")
    private String satisfaction;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "response_nurse")
    private String responseNurse;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private UserEntity parent;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private UserEntity nurse;

    @ManyToOne
    @JoinColumn(name = "vaccine_result_id")
    private VaccineResultEntity vaccineResult;

    @ManyToOne
    @JoinColumn(name = "health_result_id")
    private HealthCheckResultEntity healthResult;
}

