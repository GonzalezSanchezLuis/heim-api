package com.heim.api.payment.infraestructure.repository;

import com.heim.api.payment.domain.Payment;
import com.heim.api.payment.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMoveId(Long moveId);
    List<Payment> findAllByStatus(PaymentStatus status);
    Optional<Payment> findByMoveIdAndStatus(Long moveId, PaymentStatus status);

}
