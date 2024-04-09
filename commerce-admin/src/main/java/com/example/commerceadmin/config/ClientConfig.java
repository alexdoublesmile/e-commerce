package com.example.commerceadmin.config;

import com.example.commerceadmin.client.RestProductClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestProductClient restProductClient(
            @Value("${commerce-service.host}") String host,
            @Value("${commerce-service.port}") String port,
            @Value("${commerce-service.username}") String username,
            @Value("${commerce-service.password}") String password
    ) {
        return new RestProductClient(RestClient.builder()
                .baseUrl(host + ":" + port)
                .requestInterceptor(new BasicAuthenticationInterceptor(username, password))
                .build());
    }
}
