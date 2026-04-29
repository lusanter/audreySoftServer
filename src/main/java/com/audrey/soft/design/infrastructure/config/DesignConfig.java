package com.audrey.soft.design.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DesignConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
