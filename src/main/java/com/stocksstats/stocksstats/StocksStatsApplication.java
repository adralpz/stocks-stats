package com.stocksstats.stocksstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StocksStatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StocksStatsApplication.class, args);
    }

}
