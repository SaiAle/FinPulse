package com.finpulse.repository;

import com.finpulse.entity.Portfolio;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PortfolioRepository extends ReactiveCrudRepository<Portfolio, Long> {
    Flux<Portfolio> findByUserId(String userId);
}
