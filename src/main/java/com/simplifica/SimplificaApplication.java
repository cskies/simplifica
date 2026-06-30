package com.simplifica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimplificaApplication {
    public static void main(String[] args) {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            System.setProperty("spring.datasource.url", "jdbc:" + databaseUrl);
        }
        SpringApplication.run(SimplificaApplication.class, args);
    }
}
