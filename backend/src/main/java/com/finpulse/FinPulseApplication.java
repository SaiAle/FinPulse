package com.finpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinPulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinPulseApplication.class, args);
    }
}
