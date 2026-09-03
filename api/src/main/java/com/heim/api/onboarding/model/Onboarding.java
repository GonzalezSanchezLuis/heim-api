package com.heim.api.onboarding.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "onboarding_surveys")
public class Onboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transport_need", nullable = false)
    private String transportNeed;

    @Column(name = "registration_reason", nullable = false)
    private String registrationReason;

    @Column(name = "barrier_reason", nullable = false)
    private String barrierReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor personalizado para la creación rápida
    public Onboarding(Long userId, String transportNeed, String registrationReason, String barrierReason) {
        this.userId = userId;
        this.transportNeed = transportNeed;
        this.registrationReason = registrationReason;
        this.barrierReason = barrierReason;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}