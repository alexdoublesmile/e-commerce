package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Favourite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteRepository extends ReactiveCrudRepository<Favourite, UUID> {

    Mono<Favourite> findByProductId(Long productId);

    Mono<Void> deleteByProductId(Long id);
}
