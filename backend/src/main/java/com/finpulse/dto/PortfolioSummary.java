package com.finpulse.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PortfolioSummary {
    private String userId;
    private BigDecimal totalValue;
    private BigDecimal totalCost;
    private BigDecimal totalPnl;
    private String totalPnlPercent;
    private List<HoldingSummary> holdings;

    @Data
    @Builder
    public static class HoldingSummary {
        private String symbol;
        private BigDecimal quantity;
        private BigDecimal avgCost;
        private BigDecimal currentPrice;
        private BigDecimal marketValue;
        private BigDecimal pnl;
        private String pnlPercent;
        private String allocationPercent;
    }
}
