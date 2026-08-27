package com.heim.api.payment.application.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.heim.api.move.application.service.MoveService;
import com.heim.api.payment.application.dto.CreatePaymentDTO;
import com.heim.api.payment.application.dto.PaymentRequest;
import com.heim.api.payment.application.dto.WavaWebhookRequest;
import com.heim.api.payment.application.mapper.WavaPaymentMethodMapper;
import com.heim.api.payment.domain.Payment;
import com.heim.api.payment.domain.enums.PaymentStatus;
import com.heim.api.payment.infraestructure.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;


@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Value("${wava.api.url}")
    private String wavaApiUrl;

    @Value("${WAVA_MERCHANT_KEY}")
    private String merchantKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaymentRepository paymentRepository;
    private static final long PAYMENT_EXPIRATION_MINUTES = 60;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public String createPaymentLink(PaymentRequest paymentRequest) throws Exception {
        String json = objectMapper.writeValueAsString(paymentRequest);
        System.out.println("JSON ENVIADO: " + json);


        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(wavaApiUrl)
                .post(body)
                .addHeader("merchant-key", merchantKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("WAVA STATUS: " + response.code());
            System.out.println("WAVA RESPONSE: " + responseBody);

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Error en Wava API: " + response.code() + " " + response.message()
                );
            }
            JsonNode rootNode = objectMapper.readTree(responseBody);
           // JsonNode result = rootNode.get("result");
            //String paymentLink = rootNode.get("result").get("link").asText();
          /*  if (paymentLink.isEmpty()) {
                paymentLink = rootNode
                        .path("result")
                        .path("links")
                        .path("payment_url")
                        .asText();
            }*/

            // Sustituye esa parte por esta lógica más segura:
            JsonNode resultNode = rootNode.path("result");
            String paymentLink = "";

            if (resultNode.has("link")) {
                paymentLink = resultNode.path("link").asText();
            } else if (resultNode.has("payment_url")) {
                paymentLink = resultNode.path("payment_url").asText();
            } else if (resultNode.has("links")) {
                paymentLink = resultNode.path("links").path("payment_url").asText();
            }

            if (paymentLink.isEmpty() || "null".equals(paymentLink)) {
                logger.info("Wava no devolvió un link claro. Respuesta completa: {}", responseBody);
                // Aquí podrías lanzar una excepción personalizada para saber qué pasó
            }

            System.out.println("ENLACE DE PAGO " + paymentLink);

            return paymentLink;

        }
}

    public String createPayment(CreatePaymentDTO createPaymentDTO) throws Exception {

        Optional<Payment> existingPaymentOpt =
                paymentRepository.findByMoveId(createPaymentDTO.getMoveId());

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            validateExistingPayment(existingPayment);
            return createPaymentLink(buildWavaRequest(existingPayment));
        }

        Payment payment = paymentRepository.save(
                Payment.create(
                        createPaymentDTO.getMoveId(),
                        createPaymentDTO.getUserId(),
                        createPaymentDTO.getAmount(),
                        createPaymentDTO.getMethod(),
                        PaymentStatus.CREATED
                )
        );

        return createPaymentLink(buildWavaRequest(payment));
    }

    private void validateExistingPayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("El pago ya fue realizado");
        }

        if (isExpired(payment)) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            throw new IllegalStateException("El tiempo para pagar expiró");
        }
    }


    private PaymentRequest buildWavaRequest(Payment payment){
        PaymentRequest wavaRequest = new PaymentRequest();
        wavaRequest.setAmount(payment.getAmount());
        wavaRequest.setDescription("Pago cambio de domicilio");
        wavaRequest.setOrderKey(payment.getPaymentId());
        return  wavaRequest;
    }

    private boolean isExpired(Payment payment){
            return payment.getCreatedAt()
                    .plusMinutes(PAYMENT_EXPIRATION_MINUTES)
                    .isBefore(LocalDateTime.now());
    }

    @Transactional
    public void processWavaWebhook(WavaWebhookRequest request){
        Long myPaymentId = request.getOrderKey();

        Payment payment = paymentRepository.findById(myPaymentId)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el pago con ID: " + myPaymentId
                ));

        if (payment.getStatus() == PaymentStatus.PAID) return;

        payment.setProvider("WAVA");
        payment.setProviderOrderId(request.getIdOrder());
        payment.setMethod(WavaPaymentMethodMapper.toDomain(request.getPaymentMethod().getGateway()));


        PaymentStatus newStatus =
                "confirmed".equalsIgnoreCase(request.getStatus())
                        ? PaymentStatus.PAID
                        : PaymentStatus.FAILED;

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

    }

}
