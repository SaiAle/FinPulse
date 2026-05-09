package com.finpulse.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class MarketInsight {
    private String symbol;
    private String sentiment;   // BULLISH / BEARISH / NEUTRAL
    private String summary;
    private String signal;      // BUY / SELL / HOLD
    private String rationale;
    private Instant generatedAt;
}
