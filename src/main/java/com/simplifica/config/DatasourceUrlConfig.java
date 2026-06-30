package com.simplifica.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DatasourceUrlConfig {

    @Bean
    public DataSourceProperties dataSourceProperties(DataSourceProperties properties) {
        String url = properties.getUrl();

        if (url != null && !url.startsWith("jdbc:")) {
            properties.setUrl("jdbc:" + url);
        }

        return properties;
    }
}
