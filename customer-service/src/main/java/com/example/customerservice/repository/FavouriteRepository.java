package com.example.customerservice.repository;

import com.example.customerservice.model.entity.Favourite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteRepository {

    Flux<Favourite> findAll();

    Mono<Favourite> findByProductId(Long productId);

    Mono<Favourite> save(Favourite favourite);

    Mono<Void> deleteByProductId(Long id);
}
