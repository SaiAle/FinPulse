package com.finpulse.ai;

import com.finpulse.dto.MarketInsight;
import com.finpulse.dto.PriceQuote;
import com.finpulse.market.MarketDataService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketInsightService {

    private final ChatLanguageModel chatModel;
    private final MarketDataService marketDataService;

    public Mono<MarketInsight> generateInsight(String symbol) {
        return marketDataService.getQuote(symbol)
            .flatMap(quote -> Mono.fromCallable(() -> callAI(symbol, quote))
                .subscribeOn(Schedulers.boundedElastic()))
            .onErrorResume(e -> {
                log.warn("AI insight failed for {}: {}", symbol, e.getMessage());
                return Mono.just(fallbackInsight(symbol));
            });
    }

    private MarketInsight callAI(String symbol, PriceQuote quote) {
        String prompt = "Analyze this stock briefly:\n"
            + "Symbol: " + symbol + "\nPrice: $" + quote.getPrice()
            + "\nChange: " + quote.getChangePercent() + "%"
            + "\nVolume: " + quote.getVolume()
            + "\nHigh: $" + quote.getHigh() + " | Low: $" + quote.getLow()
            + "\nRespond in exactly this JSON (no markdown):"
            + "{\"sentiment\":\"BULLISH|BEARISH|NEUTRAL\",\"signal\":\"BUY|SELL|HOLD\","
            + "\"summary\":\"1 sentence\",\"rationale\":\"2 sentences max\"}";
        String response = chatModel.generate(prompt);
        try {
            String sentiment = extract(response, "sentiment");
            String signal    = extract(response, "signal");
            String summary   = extract(response, "summary");
            String rationale = extract(response, "rationale");
            return MarketInsight.builder().symbol(symbol).sentiment(sentiment)
                .signal(signal).summary(summary).rationale(rationale)
                .generatedAt(Instant.now()).build();
        } catch (Exception e) { return fallbackInsight(symbol); }
    }

    private String extract(String json, String key) {
        int start = json.indexOf('"' + key + "\":\"") + key.length() + 4;
        int end   = json.indexOf('"', start);
        return (start > key.length() + 3 && end > start) ? json.substring(start, end) : "N/A";
    }

    private MarketInsight fallbackInsight(String symbol) {
        return MarketInsight.builder().symbol(symbol).sentiment("NEUTRAL").signal("HOLD")
            .summary("Market data is being analyzed.")
            .rationale("Real-time AI insights require a valid OpenAI API key.")
            .generatedAt(Instant.now()).build();
    }
}
