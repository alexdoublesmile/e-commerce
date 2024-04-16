package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Favourite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteRepository extends ReactiveCrudRepository<Favourite, UUID> {

    Flux<Favourite> findAllByUserId(String userId);

    Mono<Void> deleteByProductIdAndUserId(Long id, String userId);

    Mono<Favourite> findByProductIdAndUserId(Long productId, String userId);
}
