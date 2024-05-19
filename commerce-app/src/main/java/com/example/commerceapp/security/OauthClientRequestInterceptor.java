package com.example.commerceapp.security;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;

@RequiredArgsConstructor
public class OauthClientRequestInterceptor implements ClientHttpRequestInterceptor {

    // manager for authorize user & get his access token (injected manually)
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    private final String registrationId;

    // security ctx for getting user info (principal)
    @Setter // for overriding default strategy if needs
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final HttpHeaders headers = request.getHeaders();
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            // authorize current user (principal) with specific registration id
            final OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(registrationId)
                    .principal(securityContextHolderStrategy.getContext().getAuthentication())
                    .build());

            // add header 'Authorization=Bearer ' with access token value
            headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        }

        return execution.execute(request, body);
    }
}
