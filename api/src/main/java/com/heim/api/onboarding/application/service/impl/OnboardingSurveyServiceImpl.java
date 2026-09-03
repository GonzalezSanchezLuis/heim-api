package com.heim.api.onboarding.application.service.impl;

import jakarta.transaction.Transactional;
import com.heim.api.onboarding.application.DTO.OnboardingSurveyRequest;
import com.heim.api.onboarding.application.service.OnboardingSurveyService;
import com.heim.api.onboarding.infraestructure.repository.OnboardingSurveyRepository;
import com.heim.api.onboarding.model.Onboarding;
import org.springframework.stereotype.Service;

@Service
public class OnboardingSurveyServiceImpl implements OnboardingSurveyService {
    private final OnboardingSurveyRepository surveyRepository;

    public OnboardingSurveyServiceImpl(OnboardingSurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }
    @Override
    @Transactional
    public void saveSurvey(Long userId, OnboardingSurveyRequest request) {
        if (surveyRepository.existsByUserId(userId)) {
            throw new IllegalStateException("El usuario ya completó la encuesta de inicio.");
        }

        Onboarding survey = new Onboarding(
                userId,
                request.getTransportNeed(),
                request.getRegistrationReason(),
                request.getBarrierReason()
        );

        surveyRepository.save(survey);
    }

    @Override
    @Transactional()
    public boolean hasCompletedSurvey(Long userId) {
        return surveyRepository.existsByUserId(userId);
    }

}
