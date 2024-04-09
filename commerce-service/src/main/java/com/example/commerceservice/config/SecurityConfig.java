package com.example.commerceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAuthority("SCOPE_edit_products")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAuthority("SCOPE_edit_products")
                        .requestMatchers(HttpMethod.PATCH, "/products/**").hasAuthority("SCOPE_edit_products")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("SCOPE_edit_products")
                        .requestMatchers(HttpMethod.GET, "/products/**").hasAuthority("SCOPE_view_products")
                        .anyRequest().denyAll())
                .csrf(CsrfConfigurer::disable)
                // don't restore http sessions by servlet session
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults()))
                .build();
    }
}
