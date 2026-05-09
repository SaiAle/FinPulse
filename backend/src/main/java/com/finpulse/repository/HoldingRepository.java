package com.finpulse.repository;

import com.finpulse.entity.Holding;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface HoldingRepository extends ReactiveCrudRepository<Holding, Long> {
    Flux<Holding> findByPortfolioId(Long portfolioId);
}
