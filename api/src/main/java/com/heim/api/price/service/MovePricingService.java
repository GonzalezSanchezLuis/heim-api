package com.heim.api.price.service;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.price.domain.entity.MovePricing;
import com.heim.api.price.infraestructure.repository.MovePricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MovePricingService {

    private final MovePricingRepository movePricingRepository;
    private final MoveRepository moveRepository;

    @Value("${pricing.first-trip-discount-percentage:20}")
    private int discountPercentage;

    @Value("${pricing.first-trip-discount-max:20000}")
    private BigDecimal maxDiscountAmount;

    public boolean isFirstTrip(Long userId) {
        return !moveRepository.existsByUser_UserIdAndStatus(userId, MoveStatus.MOVE_COMPLETE);
    }

    public BigDecimal applyDiscount(BigDecimal basePrice, boolean firstTrip) {
        if (!firstTrip) return BigDecimal.ZERO;
        BigDecimal discount = basePrice.multiply(BigDecimal.valueOf(discountPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return discount.min(maxDiscountAmount);
    }

    public void savePricing(Move move, BigDecimal basePrice, boolean firstTrip) {
        BigDecimal discountAmount = applyDiscount(basePrice, firstTrip);
        BigDecimal finalPrice = basePrice.subtract(discountAmount);

        MovePricing pricing = new MovePricing(
                null,
                move,
                basePrice,
                firstTrip ? discountPercentage : 0,
                discountAmount,
                finalPrice,
                firstTrip
        );
        movePricingRepository.save(pricing);
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }
}
