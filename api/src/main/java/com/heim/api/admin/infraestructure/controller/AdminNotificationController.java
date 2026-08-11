package com.heim.api.admin.infraestructure.controller;

import com.heim.api.admin.application.dto.AdminNotificationRequest;
import com.heim.api.admin.application.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody AdminNotificationRequest request) {
        adminNotificationService.sendBroadcast(request);
        return ResponseEntity.ok("Notificación enviada correctamente");
    }
}
