package com.heim.api.admin.application.service;

import com.heim.api.admin.application.dto.AdminNotificationRequest;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.notification.infraestructure.firebase.FirebaseNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseNotificationSender firebaseNotificationSender;

    @Async
    public void sendBroadcast(AdminNotificationRequest request) {
        List<FcmToken> fcmTokens = switch (request.getTarget()) {
            case "ALL_USERS"     -> fcmTokenRepository.findAllByOwnerType(FcmToken.OwnerType.USER);
            case "ALL_DRIVERS"   -> fcmTokenRepository.findAllByOwnerType(FcmToken.OwnerType.DRIVER);
            case "SPECIFIC_USER" -> fcmTokenRepository.findAllByOwnerIdAndOwnerType(request.getUserId(), FcmToken.OwnerType.USER);
            default -> List.of();
        };

        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getToken)
                .filter(t -> t != null && !t.isBlank())
                .toList();

        if (tokens.isEmpty()) {
            log.warn("⚠️ No hay tokens disponibles para el target: {}", request.getTarget());
            return;
        }

        firebaseNotificationSender.sendNotifications(tokens, request.getTitle(), request.getBody(), null, null);
        log.info("📢 Notificación enviada a {} tokens - target: {}", tokens.size(), request.getTarget());
    }
}
