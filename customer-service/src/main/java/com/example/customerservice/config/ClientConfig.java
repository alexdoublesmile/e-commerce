package com.example.customerservice.config;

import com.example.customerservice.client.FavouriteWebClient;
import com.example.customerservice.client.ProductWebClient;
import com.example.customerservice.client.ReviewWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder webClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        final ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrationRepository, authorizedClientRepository
        );
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder().filter(filter);
    }

    @Bean
    public ProductWebClient productWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${commerce-service.host}") String serviceHost,
            @Value("${commerce-service.port}") String servicePort) {
        return new ProductWebClient(webClientBuilder
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }

    @Bean
    public ReviewWebClient reviewWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${feedback-service.host}") String serviceHost,
            @Value("${feedback-service.port}") String servicePort) {
        return new ReviewWebClient(webClientBuilder
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }

    @Bean
    public FavouriteWebClient favouriteWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${feedback-service.host}") String serviceHost,
            @Value("${feedback-service.port}") String servicePort) {
        return new FavouriteWebClient(webClientBuilder
                .baseUrl(serviceHost + ":" + servicePort)
                .build());
    }
}
