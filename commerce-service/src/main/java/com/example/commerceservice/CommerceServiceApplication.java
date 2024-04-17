package com.example.commerceservice;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SecurityScheme(
        name = "keycloak",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${keycloak.uri}/realms/commerce/protocol/openid-connect/auth",
                tokenUrl = "${keycloak.uri}/realms/commerce/protocol/openid-connect/token",
                scopes = {
                        @OAuthScope(name = "openid"),
                        @OAuthScope(name = "view_products"),
                        @OAuthScope(name = "edit_products"),
                        @OAuthScope(name = "microprofile-jwt")
                }
        ))
)
@SpringBootApplication
public class CommerceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceServiceApplication.class, args);
    }

}
