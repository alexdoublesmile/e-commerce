package com.example.commerceapp.config;

import com.example.commerceapp.client.RestProductClient;
import com.example.commerceapp.security.OauthClientRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestProductClient restProductClient(
            @Value("${commerce-service.host}") String host,
            @Value("${commerce-service.port}") String port,
            // oauth id for user registration
            @Value("${commerce-service.registration-id}") String registrationId,
            // repo of registered clients from app config
            ClientRegistrationRepository clientRegistrationRepository,
            // repo of authorized clients from each request
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
    ) {
        return new RestProductClient(RestClient.builder()
                .baseUrl(host + ":" + port)
                // add access token from authorized user to interaction between services (to each http request)
                .requestInterceptor(new OauthClientRequestInterceptor(
                        new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository),
                        registrationId
                ))
                .build());
    }
}
