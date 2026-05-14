package com.heim.api.payment.application.mapper;

import com.heim.api.payment.domain.enums.PaymentMethod;

public class WavaPaymentMethodMapper {
    private WavaPaymentMethodMapper() {}

    public static PaymentMethod toDomain(String gateway) {
        return switch (gateway.toUpperCase()) {
            case "NEQUI" -> PaymentMethod.NEQUI;
            case "PSE" -> PaymentMethod.PSE;
            case "DAVIPLATA" -> PaymentMethod.DAVIPLATA;
            default -> throw new IllegalArgumentException(
                    "Gateway Wava no soportado: " + gateway
            );
        };
    }
}
