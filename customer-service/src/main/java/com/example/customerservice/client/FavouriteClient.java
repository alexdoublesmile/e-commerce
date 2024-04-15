package com.example.customerservice.client;

import com.example.customerservice.model.entity.Favourite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteClient {
    Flux<Favourite> findAllFavourite();

    Mono<Favourite> findFavouriteByProductId(Long productId);

    Mono<Favourite> addFavourite(Long id);

    Mono<Void> removeFavourite(Long id);
}
