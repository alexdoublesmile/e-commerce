package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Favourite;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryFavouriteRepository {

    private final List<Favourite> favouriteList = new CopyOnWriteArrayList<>();

    public Mono<Favourite> save(Favourite favourite) {
        favouriteList.add(favourite);
        return Mono.just(favourite);
    }

    public Mono<Void> deleteByProductId(Long productId) {
        favouriteList.removeIf(favourite -> favourite.getProductId() == productId);
        return Mono.empty();
    }

    public Mono<Favourite> findByProductId(Long productId) {
        return Flux.fromIterable(favouriteList)
                .filter(favourite ->  favourite.getProductId() == productId)
                .singleOrEmpty();
    }

    public Flux<Favourite> findAll() {
        return Flux.fromIterable(favouriteList);
    }
}
