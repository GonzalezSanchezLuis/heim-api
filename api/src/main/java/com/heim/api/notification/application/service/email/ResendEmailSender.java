package com.heim.api.notification.application.service.email;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailSender implements EmailSender {

    private final Resend resend;
    private final String defaultFrom;

    @Autowired
    public ResendEmailSender(
            @Value("${resend.api.key}") String apiKey,
            @Value("${app.email.from:onboarding@resend.dev}") String defaultFrom) {
        this.resend = new Resend(apiKey);
        this.defaultFrom =  defaultFrom;

        if (apiKey == null || apiKey.isBlank()) {
            // Esto te avisará clarito en el log del servidor qué falta
            throw new IllegalArgumentException("ERROR: La API Key de Resend no está configurada. Revisa tus variables de entorno.");
        }
    }


    @Override
    public void send(String to, String subject, String html, String from) {
        String sender = (from != null && !from.isBlank()) ? from : defaultFrom;
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(html)
                .build();
        try {
            resend.emails().send(params);
        } catch (Exception e) {
            // Loguear es mejor que solo imprimir en consola
            throw new RuntimeException("Error enviando email a: " + to, e);
        }
    }
}
