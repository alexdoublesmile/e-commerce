package com.example.customerservice.repository;

import com.example.customerservice.model.entity.FavouriteProduct;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryFavouriteRepository implements FavouriteRepository {

    private final List<FavouriteProduct> favouriteList = new CopyOnWriteArrayList<>();

    @Override
    public Mono<FavouriteProduct> save(FavouriteProduct favouriteProduct) {
        favouriteList.add(favouriteProduct);
        return Mono.just(favouriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(Long productId) {
        favouriteList.removeIf(favouriteProduct -> favouriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(Long productId) {
        return Flux.fromIterable(favouriteList)
                .filter(favouriteProduct ->  favouriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(favouriteList);
    }
}
