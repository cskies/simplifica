package com.simplifica.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;

public class DatabaseConfig implements SpringApplicationRunListener {

    public DatabaseConfig(SpringApplication springApplication, String[] args) {}

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            String jdbcUrl = "jdbc:" + databaseUrl;
            System.setProperty("spring.datasource.url", jdbcUrl);
        }
    }
}
