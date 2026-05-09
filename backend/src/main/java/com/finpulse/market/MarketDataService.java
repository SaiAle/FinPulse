package com.finpulse.market;

import com.finpulse.dto.PriceQuote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final AlphaVantageClient alphaVantageClient;
    private final ReactiveRedisTemplate<String, PriceQuote> redisTemplate;

    private static final Duration CACHE_TTL = Duration.ofSeconds(15);

    public Mono<PriceQuote> getQuote(String symbol) {
        String key = "quote:" + symbol.toUpperCase();
        return redisTemplate.opsForValue().get(key)
            .switchIfEmpty(
                alphaVantageClient.getQuote(symbol)
                    .flatMap(q -> redisTemplate.opsForValue()
                        .set(key, q, CACHE_TTL)
                        .thenReturn(q))
            )
            .doOnError(e -> log.error("Quote error for {}", symbol, e));
    }
}
