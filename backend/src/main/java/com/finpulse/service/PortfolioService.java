package com.finpulse.service;

import com.finpulse.dto.PortfolioSummary;
import com.finpulse.entity.Holding;
import com.finpulse.entity.Portfolio;
import com.finpulse.market.MarketDataService;
import com.finpulse.repository.HoldingRepository;
import com.finpulse.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final MarketDataService marketDataService;

    public Flux<Portfolio> getUserPortfolios(String userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public Mono<PortfolioSummary> getPortfolioSummary(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId)
            .collectList()
            .flatMap(holdings -> enrichWithPrices(holdings));
    }

    private Mono<PortfolioSummary> enrichWithPrices(List<Holding> holdings) {
        if (holdings.isEmpty()) {
            return Mono.just(PortfolioSummary.builder()
                .totalValue(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .totalPnl(BigDecimal.ZERO)
                .totalPnlPercent("0.00")
                .holdings(List.of())
                .build());
        }
        return Flux.fromIterable(holdings)
            .flatMap(h -> marketDataService.getQuote(h.getSymbol())
                .map(q -> {
                    BigDecimal marketValue = q.getPrice().multiply(h.getQuantity());
                    BigDecimal cost = h.getAvgCostPerShare().multiply(h.getQuantity());
                    BigDecimal pnl = marketValue.subtract(cost);
                    String pnlPct = cost.compareTo(BigDecimal.ZERO) > 0
                        ? pnl.divide(cost, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString()
                        : "0.00";
                    return PortfolioSummary.HoldingSummary.builder()
                        .symbol(h.getSymbol())
                        .quantity(h.getQuantity())
                        .avgCost(h.getAvgCostPerShare())
                        .currentPrice(q.getPrice())
                        .marketValue(marketValue.setScale(2, RoundingMode.HALF_UP))
                        .pnl(pnl.setScale(2, RoundingMode.HALF_UP))
                        .pnlPercent(pnlPct)
                        .build();
                }))
            .collectList()
            .map(summaries -> {
                BigDecimal totalValue = summaries.stream()
                    .map(PortfolioSummary.HoldingSummary::getMarketValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalCost = summaries.stream()
                    .map(s -> s.getAvgCost().multiply(s.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalPnl = totalValue.subtract(totalCost).setScale(2, RoundingMode.HALF_UP);
                String pnlPct = totalCost.compareTo(BigDecimal.ZERO) > 0
                    ? totalPnl.divide(totalCost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP).toPlainString()
                    : "0.00";
                summaries.forEach(s -> {
                    String alloc = totalValue.compareTo(BigDecimal.ZERO) > 0
                        ? s.getMarketValue().divide(totalValue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(1, RoundingMode.HALF_UP).toPlainString()
                        : "0.0";
                    s.setAllocationPercent(alloc);
                });
                return PortfolioSummary.builder()
                    .totalValue(totalValue.setScale(2, RoundingMode.HALF_UP))
                    .totalCost(totalCost.setScale(2, RoundingMode.HALF_UP))
                    .totalPnl(totalPnl)
                    .totalPnlPercent(pnlPct)
                    .holdings(summaries)
                    .build();
            });
    }

    public Mono<Holding> addHolding(Long portfolioId, String symbol,
            BigDecimal quantity, BigDecimal avgCost) {
        Holding h = new Holding();
        h.setPortfolioId(portfolioId);
        h.setSymbol(symbol.toUpperCase());
        h.setQuantity(quantity);
        h.setAvgCostPerShare(avgCost);
        h.setPurchasedAt(Instant.now());
        h.setUpdatedAt(Instant.now());
        return holdingRepository.save(h);
    }
}
