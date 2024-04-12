package com.example.customerservice.client;

import com.example.customerservice.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductWebClient implements ProductClient {

    private final WebClient webClient;

    @Override
    public Flux<Product> findAll(String filter) {
        return webClient.get()
                .uri("/api/v1/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return webClient.get()
                .uri("/api/v1/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
