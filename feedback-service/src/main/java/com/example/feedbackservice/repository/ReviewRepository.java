package com.example.feedbackservice.repository;

import com.example.feedbackservice.model.entity.Review;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ReviewRepository extends ReactiveCrudRepository<Review, UUID> {

    @Query("{'productId':  ?0}")
    Flux<Review> findAllByProductId(Long productId);
}
