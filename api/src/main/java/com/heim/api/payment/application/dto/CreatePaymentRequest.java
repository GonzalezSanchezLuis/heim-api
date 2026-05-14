package com.heim.api.payment.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {
    private Long moveId;
    private Long userId;
    private BigDecimal amount;
    private String gateway;
}
