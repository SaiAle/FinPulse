package com.finpulse.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpulse.dto.PriceQuote;
import com.finpulse.market.PriceStreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceWebSocketHandler implements WebSocketHandler {

    private final PriceStreamingService streamingService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        if (query != null && query.startsWith("symbols=")) {
            Arrays.stream(query.substring(8).split(","))
                .forEach(streamingService::watch);
        }

        Mono<Void> send = session.send(
            streamingService.priceStream()
                .map(q -> {
                    try {
                        return session.textMessage(objectMapper.writeValueAsString(q));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        Mono<Void> receive = session.receive()
            .doOnNext(msg -> {
                String text = msg.getPayloadAsText();
                if (text.startsWith("WATCH:")) {
                    streamingService.watch(text.substring(6).trim());
                } else if (text.startsWith("UNWATCH:")) {
                    streamingService.unwatch(text.substring(8).trim());
                }
            })
            .then();
        return Mono.zip(send, receive).then()
            .doOnTerminate(() -> log.info("WS session closed: {}", session.getId()));
    }
}
