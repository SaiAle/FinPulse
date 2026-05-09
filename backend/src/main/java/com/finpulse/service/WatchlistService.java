package com.finpulse.service;

import com.finpulse.dto.PriceQuote;
import com.finpulse.entity.WatchlistItem;
import com.finpulse.market.MarketDataService;
import com.finpulse.market.PriceStreamingService;
import com.finpulse.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MarketDataService marketDataService;
    private final PriceStreamingService streamingService;

    public Flux<PriceQuote> getWatchlistWithPrices(String userId) {
        return watchlistRepository.findByUserId(userId)
            .flatMap(item -> {
                streamingService.watch(item.getSymbol());
                return marketDataService.getQuote(item.getSymbol());
            });
    }

    public Mono<WatchlistItem> addToWatchlist(String userId, String symbol) {
        return watchlistRepository.existsByUserIdAndSymbol(userId, symbol.toUpperCase())
            .flatMap(exists -> {
                if (exists) return watchlistRepository.findByUserId(userId)
                    .filter(w -> w.getSymbol().equals(symbol.toUpperCase()))
                    .next();
                WatchlistItem item = new WatchlistItem();
                item.setUserId(userId);
                item.setSymbol(symbol.toUpperCase());
                item.setAddedAt(Instant.now());
                streamingService.watch(symbol.toUpperCase());
                return watchlistRepository.save(item);
            });
    }

    public Mono<Void> removeFromWatchlist(String userId, String symbol) {
        return watchlistRepository.deleteByUserIdAndSymbol(userId, symbol.toUpperCase());
    }
}
