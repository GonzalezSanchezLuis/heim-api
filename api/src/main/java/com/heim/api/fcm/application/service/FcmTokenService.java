package com.heim.api.fcm.application.service;

import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.fcm.application.dto.FcmTokenRequest;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public void registerToken(FcmTokenRequest request) {
        // 1. VALIDACIÓN CORRECTA: Buscamos por user_id si es DRIVER
        boolean ownerExists = switch (request.getOwnerType()) {
            case USER -> userRepository.existsById(request.getOwnerId());
            case DRIVER -> driverRepository.existsByUserId(request.getOwnerId());
        };

        if (!ownerExists) {
            throw new IllegalArgumentException("El propietario con ID " + request.getOwnerId() +
                    " no existe en la tabla de " + request.getOwnerType());
        }

        FcmToken token = tokenRepository.findByToken(request.getToken())
                .orElse(new FcmToken());

        // 3. ACTUALIZACIÓN DE DATOS
        // Si el token ya existía como USER, aquí se sobreescribe a DRIVER (o viceversa)
        token.setToken(request.getToken());
        token.setOwnerId(request.getOwnerId());
        token.setOwnerType(request.getOwnerType());

        tokenRepository.save(token);

        System.out.println("🚀 Token sincronizado para " + request.getOwnerType() + " (User ID: " + request.getOwnerId() + ")");
    }

}
