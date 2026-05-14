package com.heim.api.payment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WavaWebhookRequest {
  @JsonProperty("id_external")
  private Long orderKey;
  @JsonProperty("id_order")
  private Long idOrder;

  private String status;
  @JsonProperty("payment_method")
  private PaymentMethodDTO paymentMethod;

  @JsonProperty("total_price")
  private BigDecimal totalPrice;
}

