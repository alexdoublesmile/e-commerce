package com.example.customerservice.repository;

import com.example.customerservice.model.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository {

    Mono<Review> save(Review review);

    Flux<Review> findAllByProductId(Long productId);
}
