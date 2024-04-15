package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryReviewRepository {

    private final List<Review> reviewList = new CopyOnWriteArrayList<>();

    public Mono<Review> save(Review review) {
        this.reviewList.add(review);
        return Mono.just(review);
    }

    public Flux<Review> findAllByProductId(Long productId) {
        return Flux.fromIterable(reviewList)
                .filter(review -> review.getProductId() == productId);
    }
}
