package com.heim.api.onboarding.infraestructure.controller;

import jakarta.validation.Valid;
import com.heim.api.onboarding.application.DTO.OnboardingSurveyRequest;
import com.heim.api.onboarding.application.service.OnboardingSurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/onboarding")
public class OnboardingSurveyController {
    private final OnboardingSurveyService surveyService;

    public OnboardingSurveyController(OnboardingSurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveSurvey(
            @Valid @RequestBody OnboardingSurveyRequest request
    ) {
        surveyService.saveSurvey(request.getUserId(),request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getSurveyStatus(
            @AuthenticationPrincipal Long currentUserId
    ) {
        boolean completed = surveyService.hasCompletedSurvey(currentUserId);
        return ResponseEntity.ok(Map.of("completed", completed));
    }
}
