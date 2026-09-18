package com.company.bookhub.integration.numverify;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NumverifyConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.apilayer.com")
                .build();
    }
}