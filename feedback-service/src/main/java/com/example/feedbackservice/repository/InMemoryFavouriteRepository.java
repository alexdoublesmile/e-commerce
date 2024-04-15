package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Favourite;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Log4j2
@Repository
public class InMemoryFavouriteRepository implements FavouriteRepository {

    private final List<Favourite> favouriteList = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Favourite> save(Favourite favourite) {
        favouriteList.add(favourite);
        return Mono.just(favourite);
    }

    @Override
    public Mono<Void> deleteByProductId(Long productId) {
        favouriteList.removeIf(favourite -> favourite.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<Favourite> findByProductId(Long productId) {
        return Flux.fromIterable(favouriteList)
                .filter(favourite ->  favourite.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<Favourite> findAll() {
        log.info("Request to DB for:{}", favouriteList);
        return Flux.fromIterable(favouriteList);
    }
}
