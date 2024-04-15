package com.example.customerservice.config;

import com.example.customerservice.client.FavouriteWebClient;
import com.example.customerservice.client.ProductWebClient;
import com.example.customerservice.client.ReviewWebClient;
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

    @Bean
    public ReviewWebClient reviewWebClient(
            @Value("${feedback-service.host}") String serviceHost,
            @Value("${feedback-service.port}") String servicePort) {
        return new ReviewWebClient(WebClient.builder()
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }

    @Bean
    public FavouriteWebClient favouriteWebClient(
            @Value("${feedback-service.host}") String serviceHost,
            @Value("${feedback-service.port}") String servicePort) {
        return new FavouriteWebClient(WebClient.builder()
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }
}
