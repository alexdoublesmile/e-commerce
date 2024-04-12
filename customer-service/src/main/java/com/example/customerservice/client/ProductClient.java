package com.example.customerservice.client;

import com.example.customerservice.model.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClient {
    Flux<Product> findAll(String filter);

    Mono<Product> findById(Long id);
}
