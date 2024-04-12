package com.example.customerservice.config;

import com.example.customerservice.client.ProductWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public ProductWebClient productWebClient(
            @Value("${commerce-service.host}") String serviceHost,
            @Value("${commerce-service.port}") String servicePort) {
        return new ProductWebClient(WebClient.builder()
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }
}
