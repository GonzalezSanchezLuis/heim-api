package com.heim.api.payment.domain;

import com.heim.api.payment.domain.enums.PaymentMethod;
import com.heim.api.payment.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long paymentId;

    private Long moveId;
    private Long userId;

    private BigDecimal amount;
    private String provider;
    private Long providerOrderId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Payment create(
            Long moveId,
            Long userId,
            BigDecimal amount,
            PaymentMethod method,
            PaymentStatus status
    ) {
        Payment payment = new Payment();
        payment.moveId = moveId;
        payment.userId = userId;
        payment.amount = amount;
        payment.method = method;
        payment.status = status;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }
}
