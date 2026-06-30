package com.simplifica.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class DatabaseConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            String jdbcUrl = "jdbc:" + databaseUrl;
            System.setProperty("spring.datasource.url", jdbcUrl);
        }
    }
}
