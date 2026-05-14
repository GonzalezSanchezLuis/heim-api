package com.heim.api.users.application.service;

import com.heim.api.auth.application.service.PasswordResetService;
import com.heim.api.drivers.application.service.DriverService;
import com.heim.api.fcm.application.dto.FcmTokenRequest;
import com.heim.api.fcm.application.service.FcmTokenService;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.fcm.infraestructure.repository.FcmTokenRepository;
import com.heim.api.notification.application.service.EmailNotificationService;
import com.heim.api.users.application.dto.UpdateUserDataRequestDTO;
import com.heim.api.users.application.dto.UserRequest;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.application.mapper.UserMapper;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;
    private final EmailNotificationService emailNotificationService;
    private final FcmTokenRepository fcmTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       BCryptPasswordEncoder bcryptPasswordEncoder,
                       FcmTokenService fcmTokenService,
                       EmailNotificationService emailNotificationService,
                       FcmTokenRepository fcmTokenRepository
                       ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.emailNotificationService =  emailNotificationService;
        this.fcmTokenRepository = fcmTokenRepository;
    }


    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyRegisteredException("Ya existe un usuario registrado con ese email.");
        }
        User newUser = userMapper.toEntity(userRequest);
        newUser.setPassword(bcryptPasswordEncoder.encode(userRequest.getPassword()));
        newUser.setActive(true);
        newUser.setRole("USER");
        User savedUser = userRepository.save(newUser);

        if (userRequest.getFcmToken() != null && !userRequest.getFcmToken().isEmpty()){
            FcmToken tokenEntity = fcmTokenRepository.findByToken(userRequest.getFcmToken())
                    .orElse(new FcmToken());

            tokenEntity.setToken(userRequest.getFcmToken());
            tokenEntity.setOwnerId(savedUser.getUserId());
            tokenEntity.setOwnerType(FcmToken.OwnerType.USER);

            fcmTokenRepository.save(tokenEntity);
            logger.info("✅ Token vinculado automáticamente al nuevo usuario: {}", savedUser.getEmail());
        }

        try {
            emailNotificationService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());
        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }

        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long userId) throws NoSuchElementException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return userMapper.toResponse(user);
    }



    public UserResponse updateUserData(Long userId, UpdateUserDataRequestDTO userRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        if (userRequest.getPassword() != null &&  !userRequest.getPassword().isEmpty()){
            userRequest.setPassword(bcryptPasswordEncoder.encode(userRequest.getPassword()));
        }

        user.setFullName(userRequest.getFullName());
        user.setDocument(userRequest.getDocument());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setUrlAvatarProfile(userRequest.getUrlAvatarProfile());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }


    public void userDelete(Long userId){
        User userToDelete = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        userRepository.delete(userToDelete);
    }

}
