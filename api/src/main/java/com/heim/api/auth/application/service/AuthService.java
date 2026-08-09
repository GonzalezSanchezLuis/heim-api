package com.heim.api.auth.application.service;

import com.heim.api.exceptions.DatabaseUnavailableException;
import com.heim.api.exceptions.IncorrectCredentialsException;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.application.mapper.UserMapper;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.jwt.JwtUtils;
import com.heim.api.users.infraestructure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }

    public UserResponse authenticate(String email, String password) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                throw new IncorrectCredentialsException("Invalid credentials provided.");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new IncorrectCredentialsException("Invalid credentials provided.");
            }

            UserResponse response = userMapper.toResponse(user);
            String token = jwtUtils.generateToken(user.getEmail());
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("🔑 Token generado para {}: {}", user.getEmail(), token);
            response.setToken(token);
            return response;

        } catch (DataAccessException e) {
            throw new DatabaseUnavailableException("La base de datos no está disponible.", e);
        }
    }
}
