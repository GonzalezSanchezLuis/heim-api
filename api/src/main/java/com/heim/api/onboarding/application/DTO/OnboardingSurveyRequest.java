package com.heim.api.onboarding.application.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingSurveyRequest{
        private Long userId;
        private String transportNeed;
        private String registrationReason;
        private String barrierReason;
}


