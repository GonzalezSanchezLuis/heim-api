package com.heim.api.users.infraestructure.controller;

import com.heim.api.auth.application.service.AuthService;
import com.heim.api.users.application.dto.UserResponse;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import com.heim.api.users.infraestructure.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthService authService, JwtUtils jwtUtils, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("auth")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String password = request.get("password");

        UserResponse userResponse = authService.authenticate(email, password);
        session.setAttribute("userId", userResponse.getUserId());
        session.setAttribute("role", userResponse.getRole());

        return ResponseEntity.ok(userResponse);
    }



    @PostMapping("logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout exitoso");
    }

    @GetMapping("validate/me")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
        }
        try {
            String token = authHeader.substring(7);
            String email = jwtUtils.extractUserEmail(token);
            return userRepository.findByEmail(email)
                    .map(user -> ResponseEntity.ok(Map.of("valid", true, "email", email, "role", user.getRole(), "userId", user.getUserId())))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("valid", false));
        }
    }

}
