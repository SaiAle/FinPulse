package com.finpulse.market;

import com.finpulse.dto.PriceQuote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceStreamingService {

    private final MarketDataService marketDataService;

    private final Sinks.Many<PriceQuote> priceSink =
        Sinks.many().multicast().onBackpressureBuffer(256);

    private final Set<String> watchedSymbols = ConcurrentHashMap.newKeySet();

    public Flux<PriceQuote> priceStream() {
        return priceSink.asFlux();
    }

    public void watch(String symbol) {
        watchedSymbols.add(symbol.toUpperCase());
        log.info("Watching symbol: {}", symbol);
    }

    public void unwatch(String symbol) {
        watchedSymbols.remove(symbol.toUpperCase());
    }

    @Scheduled(fixedDelay = 5000)
    public void refreshPrices() {
        if (watchedSymbols.isEmpty()) return;
        watchedSymbols.forEach(symbol ->
            marketDataService.getQuote(symbol)
                .subscribe(quote -> priceSink.tryEmitNext(quote))
        );
    }
}
