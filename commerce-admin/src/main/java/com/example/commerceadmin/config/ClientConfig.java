package com.example.commerceadmin.config;

import com.example.commerceadmin.client.RestProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestProductClient restProductClient(
            @Value("${commerce-service.url:http://localhost:8080}") String serviceBaseUri) {
        return new RestProductClient(RestClient.builder()
                .baseUrl(serviceBaseUri)
                .build());
    }
}
