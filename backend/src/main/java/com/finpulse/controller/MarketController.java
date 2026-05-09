package com.finpulse.controller;

import com.finpulse.ai.MarketInsightService;
import com.finpulse.dto.MarketInsight;
import com.finpulse.dto.PriceQuote;
import com.finpulse.market.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;
    private final MarketInsightService insightService;

    @GetMapping("/quote/{symbol}")
    public Mono<PriceQuote> getQuote(@PathVariable String symbol) {
        return marketDataService.getQuote(symbol.toUpperCase());
    }

    @GetMapping("/quotes")
    public Flux<PriceQuote> getQuotes(@RequestParam List<String> symbols) {
        return Flux.fromIterable(symbols)
            .flatMap(s -> marketDataService.getQuote(s.toUpperCase()));
    }

    @GetMapping("/insight/{symbol}")
    public Mono<MarketInsight> getInsight(@PathVariable String symbol) {
        return insightService.generateInsight(symbol.toUpperCase());
    }
}
