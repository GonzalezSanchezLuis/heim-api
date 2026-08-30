package com.heim.api.price.domain.entity;

import com.heim.api.move.domain.entity.Move;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "move_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "move_id", nullable = false, unique = true)
    private Move move;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private int discountPercentage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private boolean isFirstTrip;
}
