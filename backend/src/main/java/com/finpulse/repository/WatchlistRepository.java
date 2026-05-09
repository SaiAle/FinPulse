package com.finpulse.repository;

import com.finpulse.entity.WatchlistItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WatchlistRepository extends ReactiveCrudRepository<WatchlistItem, Long> {
    Flux<WatchlistItem> findByUserId(String userId);
    Mono<Void> deleteByUserIdAndSymbol(String userId, String symbol);
    Mono<Boolean> existsByUserIdAndSymbol(String userId, String symbol);
}
