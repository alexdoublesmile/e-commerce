package com.example.customerservice.repository;

import com.example.customerservice.model.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteRepository {

    Flux<FavouriteProduct> findAll();

    Mono<FavouriteProduct> findByProductId(Long productId);

    Mono<FavouriteProduct> save(FavouriteProduct favouriteProduct);

    Mono<Void> deleteByProductId(Long id);
}
