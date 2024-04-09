package com.example.commerceadmin.config;

import com.example.commerceadmin.client.RestProductClient;
import com.example.commerceadmin.security.OauthClientRequestInterceptor;
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
            @Value("${commerce-service.registration-id}") String registrationId,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
    ) {
        return new RestProductClient(RestClient.builder()
                .baseUrl(host + ":" + port)
                .requestInterceptor(new OauthClientRequestInterceptor(
                        new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository),
                        registrationId
                ))
                .build());
    }
}
