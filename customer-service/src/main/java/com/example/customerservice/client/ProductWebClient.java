package com.example.customerservice.client;

import com.example.customerservice.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class ProductWebClient implements ProductClient {

    private final WebClient webClient;

    @Override
    public Flux<Product> findAll(String filter) {
        return webClient.get()
                .uri("/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
