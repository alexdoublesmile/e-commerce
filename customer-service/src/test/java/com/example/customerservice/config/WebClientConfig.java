package com.example.customerservice.config;

import com.example.customerservice.client.FavouriteWebClient;
import com.example.customerservice.client.ProductWebClient;
import com.example.customerservice.client.ReviewWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class WebClientConfig {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return mock();
    }

    @Bean
    @Primary
    public ProductWebClient mockWebClientProductsClient() {
        return new ProductWebClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public FavouriteWebClient mockWebClientFavouriteProductsClient() {
        return new FavouriteWebClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public ReviewWebClient mockWebClientProductReviewsClient() {
        return new ReviewWebClient(WebClient.builder()
                .baseUrl("http://localhost:54321")
                .build());
    }
}
