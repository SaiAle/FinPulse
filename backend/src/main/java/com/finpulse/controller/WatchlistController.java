package com.finpulse.controller;

import com.finpulse.dto.PriceQuote;
import com.finpulse.entity.WatchlistItem;
import com.finpulse.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping("/{userId}")
    public Flux<PriceQuote> getWatchlist(@PathVariable String userId) {
        return watchlistService.getWatchlistWithPrices(userId);
    }

    @PostMapping("/{userId}")
    public Mono<WatchlistItem> addSymbol(@PathVariable String userId,
            @RequestBody Map<String, String> body) {
        return watchlistService.addToWatchlist(userId, body.get("symbol"));
    }

    @DeleteMapping("/{userId}/{symbol}")
    public Mono<Void> removeSymbol(@PathVariable String userId,
            @PathVariable String symbol) {
        return watchlistService.removeFromWatchlist(userId, symbol);
    }
}
