package com.heim.api.fcm.infraestructure.controller;

import com.heim.api.fcm.application.dto.FcmTokenRequest;
import com.heim.api.fcm.application.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fcm")
@CrossOrigin("*")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> registerToken(@RequestBody FcmTokenRequest request) {
        tokenService.registerToken(request);
        return ResponseEntity.ok("Token registrado correctamente");
    }
}
