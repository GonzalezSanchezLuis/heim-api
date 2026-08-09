package com.heim.api.notification.application.service;

import com.heim.api.notification.application.service.email.EmailSender;
import com.heim.api.notification.application.service.email.templates.AccountStatusTemplate;
import com.heim.api.notification.application.service.email.templates.AuthEmailTemplate;
import com.heim.api.notification.application.service.email.templates.CancelledMoveEmailTemplate;
import com.heim.api.notification.application.service.email.templates.ScheduledMoveEmailTemplate;
import com.heim.api.notification.application.service.email.templates.WelcomeEmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private final EmailSender emailSender;
    private final String BRAND_COLOR = "#4F46E5";
    @Value("${app.email.from.auth}")
    private String fromAuth;

    @Value("${app.url.frontend}")
    private String frontend;

    @Value("${app.email.from.onboarding}")
    private String fromOnboarding;

    @Value("${app.email.from.status}")
    private String fromStatus;

    @Async
    public void sendPasswordResetNotification(String email, String name, String token) {
      //  String emailDePrueba = "test-ttttan59i@srv1.mail-tester.com";
        String html = AuthEmailTemplate.buildPasswordReset(name, token);
        emailSender.send(email, "Vamos a ayudarte a volver a entrar", html, fromAuth);
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        String html = WelcomeEmailTemplate.build(name);
        emailSender.send(toEmail, "¡Bienvenido a Heim!", html, fromOnboarding);
    }

    @Async
    public void sendAccountStatusEmail(String toEmail, String firstName, boolean isActive){
        String htmlContent = AccountStatusTemplate.build(firstName, isActive);
        String subject = isActive ? "¡Tu cuenta en Heim está activa!" : "Tu cuenta en Heim ha sido pausada";
        emailSender.send(toEmail, subject, htmlContent, fromStatus);
    }

    @Async
    public void sendScheduledMoveEmail(String toEmail, String firstName, String origin, String destination, LocalDateTime scheduledTime) {
        String html = ScheduledMoveEmailTemplate.build(firstName, origin, destination, scheduledTime);
        emailSender.send(toEmail, "✅ Tu viaje ha sido programado", html, fromOnboarding);
    }

    @Async
    public void sendCancelledMoveEmail(String toEmail, String firstName, String origin, String destination, LocalDateTime scheduledTime) {
        String html = CancelledMoveEmailTemplate.build(firstName, origin, destination, scheduledTime);
        emailSender.send(toEmail, "❌ Tu viaje programado ha sido cancelado", html, fromOnboarding);
    }

}
