package com.heim.api.payment.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.heim.api.users.application.dto.UserPaymentRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @JsonProperty("redirect_link")
    private String redirectLink;

    @NotNull(message = "El monto es obligatorio")
    private BigDecimal amount;

    @JsonProperty("user")
    private UserPaymentRequest userPaymentRequest;

    @JsonProperty("order_key")
    private Long  orderKey;

}
