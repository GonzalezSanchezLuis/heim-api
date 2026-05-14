package com.heim.api.notification.application.mapper;

import com.heim.api.payment.domain.enums.PayoutStatus;

public class PaymentStatusMessageMapper {
    private PaymentStatusMessageMapper(){}

    public  static String humanReadable(PayoutStatus payoutStatus){
        return  switch (payoutStatus){
            case APPROVED -> "aprobado";
            case DECLINED -> "rechazado";
            case PENDING -> "pendiente";
        };
    }
}
