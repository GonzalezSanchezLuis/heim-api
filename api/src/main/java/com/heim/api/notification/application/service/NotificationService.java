package com.heim.api.notification.application.service;

import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.notification.application.mapper.PaymentStatusMessageMapper;
import com.heim.api.notification.infraestructure.firebase.FirebaseNotificationSender;
import com.heim.api.payment.domain.enums.PayoutStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseNotificationSender firebaseNotificationSender;
    private final FcmTokenRepository fcmTokenRepository;


    public  void notify(FcmToken.OwnerType ownerType, Long ownerId, String title, String body, Map<String, String> data, String message){
        List<String> targetTokens = getTargetTokens(ownerType,ownerId);
        firebaseNotificationSender.sendNotifications(targetTokens, title, body, data, message);
    }

    public void paymentNotification(FcmToken.OwnerType ownerType,
                                    Long ownerId,
                                    PayoutStatus status,
                                    BigDecimal amount) {

        String title;
        String body;

        switch (status) {
            case APPROVED -> {
                title = "Todo está en marcha";
                body = "Tu servicio de mudanza quedó confirmado. El pago fue aprobado por "
                        + amount + " COP. Nosotros nos encargamos del resto.";
            }
            case DECLINED -> {
                title = "Algo no salió como esperábamos";
                body = "Intentamos procesar el pago de tu mudanza, pero no fue posible. "
                        + "No se realizó ningún cargo. Puedes intentarlo de nuevo cuando quieras.";
            }
            case PENDING -> {
                title = "Estamos revisándolo";
                body = "El pago de tu mudanza está en proceso por "
                        + amount + " COP. Te avisaremos apenas tengamos una respuesta.";
            }
            default -> throw new IllegalStateException("Estado no soportado");
        }

        Map<String, String> data = Map.of(
                "paymentStatus", PaymentStatusMessageMapper.humanReadable(status),
                "amount", amount.toString()
        );



        notify(ownerType,ownerId, title, body, data, null );
    }

    public void notifyUser(FcmToken.OwnerType ownerType, Long ownerId, String title, String body) {
        notify(ownerType, ownerId, title, body, null, null);
    }


    private List<String> getTargetTokens(FcmToken.OwnerType ownerType, Long ownerId) {
        return fcmTokenRepository.findAllByOwnerIdAndOwnerType(ownerId, ownerType)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }
}
