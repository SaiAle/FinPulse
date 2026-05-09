package com.finpulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.finpulse.dto.PriceQuote;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, PriceQuote> priceRedisTemplate(
            ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<PriceQuote> valueSerializer =
            new Jackson2JsonRedisSerializer<>(PriceQuote.class);
        RedisSerializationContext<String, PriceQuote> ctx =
            RedisSerializationContext.<String, PriceQuote>newSerializationContext(
                new StringRedisSerializer())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, ctx);
    }
}
