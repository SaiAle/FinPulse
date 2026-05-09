package com.finpulse.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("watchlist")
public class WatchlistItem {
    @Id private Long id;
    private String userId;
    private String symbol;
    private Instant addedAt;
}
