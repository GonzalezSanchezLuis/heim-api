package com.heim.api.onboarding.infraestructure.repository;

import com.heim.api.onboarding.model.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingSurveyRepository extends JpaRepository<Onboarding, Long> {
    boolean existsByUserId(Long userId);
    Optional<Onboarding> findByUserId(Long userId);
}
