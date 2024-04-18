package com.example.feedbackservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange(configurer -> configurer
                        .pathMatchers("/webjars/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                        .permitAll()
                        .pathMatchers("/actuator/**")
                        .hasAuthority("SCOPE_metrics")
                        .anyExchange()
                        .authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2ResourceServer(customizer -> customizer.jwt(withDefaults()))
                .oauth2Client(Customizer.withDefaults())
                .build();
    }
}
