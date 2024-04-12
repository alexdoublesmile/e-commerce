package com.example.customerservice.client;

import com.example.customerservice.model.entity.Product;
import reactor.core.publisher.Flux;

public interface ProductClient {
    Flux<Product> findAll(String filter);
}
