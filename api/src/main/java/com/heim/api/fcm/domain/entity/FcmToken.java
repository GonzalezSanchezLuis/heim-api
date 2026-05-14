package com.heim.api.fcm.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fcm_tokens", indexes = {
        @Index(name = "idx_owner", columnList = "ownerId, ownerType")
})
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(unique = true)
    private String token;

    private Long ownerId;

    @Enumerated(EnumType.STRING)
    private OwnerType ownerType;

    public enum OwnerType {
        USER,
        DRIVER
    }
}

