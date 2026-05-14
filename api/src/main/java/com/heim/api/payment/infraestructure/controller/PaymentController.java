package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.*;
import com.heim.api.payment.application.mapper.WavaPaymentMethodMapper;
import com.heim.api.payment.application.service.PaymentService;
import com.heim.api.payment.application.service.SettlementService;
import com.heim.api.payment.domain.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final SettlementService settlementService;



    @PostMapping("/create")
    public  ResponseEntity<String> createPayment(@RequestBody CreatePaymentRequest request) throws Exception {
        PaymentMethod method = WavaPaymentMethodMapper.toDomain(request.getGateway());

        CreatePaymentDTO dto = new CreatePaymentDTO(
                request.getMoveId(),
                request.getUserId(),
                request.getAmount(),
                method
        );

        return ResponseEntity.ok(paymentService.createPayment(dto));

    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayout(@RequestBody PaymentConfirmationRequest request) {
        if (request.getDriverIds() == null || request.getDriverIds().isEmpty()) {
            return ResponseEntity.badRequest().body("La lista de driverIds no puede estar vacía.");
        }

        String batchRef = request.getPaymentBatchReference() != null ? request.getPaymentBatchReference() : "MANUAL_PAYOUT_" + System.currentTimeMillis();

        try {
            settlementService.confirmPayout(request.getDriverIds(), batchRef);
            return ResponseEntity.ok("Pago confirmado y saldos actualizados para " + request.getDriverIds().size() + " conductores.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al confirmar el pago: " + e.getMessage());
        }
    }

    @PostMapping("/webhooks/wava")
    public ResponseEntity<Void> receiveWavaWebhook(@RequestBody WavaWebhookRequest payload){
        System.out.println("WEBHOOK WAVA RECIBIDO:");
        System.out.println("WEBHOOK RECIBIDO - id de la orden: " + payload.getOrderKey());
        System.out.println(payload);

        paymentService.processWavaWebhook(payload);
        return ResponseEntity.ok().build();
        }

}
