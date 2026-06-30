package com.simplifica.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            String jdbcUrl = "jdbc:" + databaseUrl;

            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", jdbcUrl);

            MapPropertySource propertySource = new MapPropertySource("databaseUrlOverride", properties);
            environment.getPropertySources().addFirst(propertySource);
        }
    }
}
