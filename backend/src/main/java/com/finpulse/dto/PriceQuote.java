package com.finpulse.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PriceQuote {
    private String symbol;
    private BigDecimal price;
    private BigDecimal change;
    private String changePercent;
    private Long volume;
    private BigDecimal high;
    private BigDecimal low;
    private Instant timestamp;
}
