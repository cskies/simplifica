package com.simplifica.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties
public class DatabaseConfig {

    @PostConstruct
    public void configureDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            String jdbcUrl = "jdbc:" + databaseUrl;
            System.setProperty("spring.datasource.url", jdbcUrl);
        }
    }
}
