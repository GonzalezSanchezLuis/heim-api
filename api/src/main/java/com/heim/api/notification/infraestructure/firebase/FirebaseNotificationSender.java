package com.heim.api.notification.infraestructure.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FirebaseNotificationSender {

    private final FcmTokenRepository fcmTokenRepository;

    public FirebaseNotificationSender(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @Async
    public void sendNotifications(List<String> tokens, String title, String body, Map<String, String> data, String message) {
        for (String token : tokens) {
            try {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                Message.Builder messageBuilder = Message.builder()
                        .setToken(token)
                        .setNotification(notification);

                if (data != null && !data.isEmpty()) {
                    messageBuilder.putAllData(data);
                }

                if (message != null) {
                    messageBuilder.putData("message", message);
                }

                String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
                log.info("✅ Notificación enviada. ID de mensaje: {}", response);

            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    log.warn("⚠️ Token FCM no registrado, eliminando de BD: {}", token);
                    fcmTokenRepository.findByToken(token).ifPresent(fcmTokenRepository::delete);
                } else {
                    log.error("❌ Error al enviar notificación: {}", e.getMessage(), e);
                }
            } catch (Exception e) {
                log.error("❌ Error inesperado al enviar notificación: {}", e.getMessage(), e);
            }
        }
    }
}
