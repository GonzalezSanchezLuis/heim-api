package com.heim.api.payment.application.dto;

import lombok.Data;

@Data
public class PaymentMethodDTO {
    private String gateway;
    private String status;
}
