package com.heim.api.onboarding.application.service;

import com.heim.api.onboarding.application.DTO.OnboardingSurveyRequest;

public interface OnboardingSurveyService {
    void saveSurvey(Long userId, OnboardingSurveyRequest request);
    boolean hasCompletedSurvey(Long userId);
}
