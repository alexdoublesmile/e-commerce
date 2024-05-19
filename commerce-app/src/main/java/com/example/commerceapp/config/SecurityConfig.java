package com.example.commerceapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(req -> req
                        .anyRequest()
                        .hasRole("MANAGER"))
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        final OidcUserService oidcUserService = new OidcUserService();
        return userRequest -> {
            final OidcUser oidcUser = oidcUserService.loadUser(userRequest);
            // add roles from 'groups' claim (microprofile-jwt scope) to default roles after auth
            final List<GrantedAuthority> authorityList = Stream.concat(
                    oidcUser.getAuthorities().stream(),
                    Optional.ofNullable(oidcUser.getClaimAsStringList("groups"))
                            .orElseGet(List::of)
                            .stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .map(SimpleGrantedAuthority::new)
                            .map(GrantedAuthority.class::cast))
                    .toList();

            return new DefaultOidcUser(authorityList, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }
}
