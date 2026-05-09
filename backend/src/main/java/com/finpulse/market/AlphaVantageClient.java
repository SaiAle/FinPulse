package com.finpulse.market;

import com.finpulse.dto.PriceQuote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlphaVantageClient {

    private final WebClient webClient = WebClient.builder()
        .baseUrl("https://www.alphavantage.co")
        .build();

    @Value("${finpulse.alpha-vantage.api-key:demo}")
    private String apiKey;

    @SuppressWarnings("unchecked")
    public Mono<PriceQuote> getQuote(String symbol) {
        return webClient.get()
            .uri(u -> u.path("/query")
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apiKey)
                .build())
            .retrieve()
            .bodyToMono(Map.class)
            .map(body -> {
                Map<String, String> quote = (Map<String, String>) body.get("Global Quote");
                if (quote == null || quote.isEmpty()) {
                    return buildFallback(symbol);
                }
                return PriceQuote.builder()
                    .symbol(symbol)
                    .price(new BigDecimal(quote.getOrDefault("05. price", "0")))
                    .change(new BigDecimal(quote.getOrDefault("09. change", "0")))
                    .changePercent(quote.getOrDefault("10. change percent", "0%").replace("%", ""))
                    .volume(Long.parseLong(quote.getOrDefault("06. volume", "0")))
                    .high(new BigDecimal(quote.getOrDefault("03. high", "0")))
                    .low(new BigDecimal(quote.getOrDefault("04. low", "0")))
                    .timestamp(Instant.now())
                    .build();
            })
            .onErrorResume(e -> {
                log.warn("Alpha Vantage error for {}: {} — using fallback", symbol, e.getMessage());
                return Mono.just(buildFallback(symbol));
            });
    }

    /** Demo/fallback data so the UI works without a real API key */
    private PriceQuote buildFallback(String symbol) {
        double base = switch (symbol.toUpperCase()) {
            case "AAPL" -> 189.50;
            case "MSFT" -> 415.20;
            case "GOOGL" -> 175.80;
            case "AMZN" -> 182.40;
            case "NVDA" -> 875.30;
            case "TSLA" -> 172.60;
            case "META"  -> 502.10;
            default -> 100.00;
        };
        double change = (Math.random() - 0.48) * 5;
        return PriceQuote.builder()
            .symbol(symbol)
            .price(BigDecimal.valueOf(base + change).setScale(2, java.math.RoundingMode.HALF_UP))
            .change(BigDecimal.valueOf(change).setScale(2, java.math.RoundingMode.HALF_UP))
            .changePercent(String.format("%.2f", change / base * 100))
            .volume((long)(1_000_000 + Math.random() * 5_000_000))
            .high(BigDecimal.valueOf(base + Math.abs(change) + 1).setScale(2, java.math.RoundingMode.HALF_UP))
            .low(BigDecimal.valueOf(base - Math.abs(change) - 1).setScale(2, java.math.RoundingMode.HALF_UP))
            .timestamp(Instant.now())
            .build();
    }
}
