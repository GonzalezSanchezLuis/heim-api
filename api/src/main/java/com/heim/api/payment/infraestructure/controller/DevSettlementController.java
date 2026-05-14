package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@CrossOrigin("*")
@Profile("dev")
public class DevSettlementController {
    private final SettlementService settlementService;

    @PostMapping("/run-settlement")
    public ResponseEntity<String> runSettlementManually() {
        settlementService.processWeeklySettlement();
        return ResponseEntity.ok("✅ Settlement ejecutado manualmente");
    }
}
