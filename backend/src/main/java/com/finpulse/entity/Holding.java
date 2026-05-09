package com.finpulse.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Table("holdings")
public class Holding {
    @Id private Long id;
    private Long portfolioId;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal avgCostPerShare;
    private Instant purchasedAt;
    private Instant updatedAt;
}
