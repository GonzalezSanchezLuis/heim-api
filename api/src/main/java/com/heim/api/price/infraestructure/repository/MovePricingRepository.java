package com.heim.api.price.infraestructure.repository;

import com.heim.api.price.domain.entity.MovePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovePricingRepository extends JpaRepository<MovePricing, Long> {
}
