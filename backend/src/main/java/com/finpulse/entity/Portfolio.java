package com.finpulse.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("portfolios")
public class Portfolio {
    @Id private Long id;
    private String userId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}
