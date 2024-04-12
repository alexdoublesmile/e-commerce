package com.example.customerservice.repository;

import com.example.customerservice.model.entity.Review;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryReviewRepository implements ReviewRepository {

    private final List<Review> reviewList = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Review> save(Review review) {
        this.reviewList.add(review);
        return Mono.just(review);
    }

    @Override
    public Flux<Review> findAllByProductId(Long productId) {
        return Flux.fromIterable(reviewList)
                .filter(review -> review.getProductId() == productId);
    }
}
