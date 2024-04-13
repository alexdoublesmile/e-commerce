package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository {

    Mono<Review> save(Review review);

    Flux<Review> findAllByProductId(Long productId);
}
