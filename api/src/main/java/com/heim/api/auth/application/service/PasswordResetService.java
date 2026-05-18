package com.heim.api.auth.application.service;

import com.heim.api.auth.domain.entity.PasswordReset;
import com.heim.api.auth.infraestructure.repository.PasswordResetRepository;
import com.heim.api.notification.application.service.EmailNotificationService;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(
            PasswordResetRepository passwordResetRepository,
            UserRepository userRepository,
            EmailNotificationService emailNotificationService,
            PasswordEncoder passwordEncoder
    ){
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.emailNotificationService = emailNotificationService;
        this.passwordEncoder = passwordEncoder;
    }


    public void createPasswordResetToken(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()){
            User user = userOptional.get();

            String token = UUID.randomUUID().toString();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setToken(token);
            passwordReset.setExpirationTime(expirationTime);
            passwordReset.setUser(user);

            passwordResetRepository.save(passwordReset);
            emailNotificationService.sendPasswordResetNotification(
                    user.getEmail(),
                    user.getFullName(),
                    token
            );

        }else {
            System.out.println("Intento de recuperación para email no registrado: " + email);
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordReset resetRequest = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o no encontrado"));

        if (resetRequest.isUsed() || resetRequest.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El token ha expirado o ya fue utilizado");
        }

        User user = resetRequest.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        resetRequest.setUsed(true);

        userRepository.save(user);
        passwordResetRepository.delete(resetRequest);
    }

    }

