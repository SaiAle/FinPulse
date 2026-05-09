package com.finpulse.controller;

import com.finpulse.dto.PortfolioSummary;
import com.finpulse.entity.Holding;
import com.finpulse.entity.Portfolio;
import com.finpulse.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/user/{userId}")
    public Flux<Portfolio> getUserPortfolios(@PathVariable String userId) {
        return portfolioService.getUserPortfolios(userId);
    }

    @GetMapping("/{id}/summary")
    public Mono<PortfolioSummary> getSummary(@PathVariable Long id) {
        return portfolioService.getPortfolioSummary(id);
    }

    @PostMapping("/{id}/holdings")
    public Mono<Holding> addHolding(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return portfolioService.addHolding(
            id,
            body.get("symbol"),
            new BigDecimal(body.get("quantity")),
            new BigDecimal(body.get("avgCost"))
        );
    }
}
