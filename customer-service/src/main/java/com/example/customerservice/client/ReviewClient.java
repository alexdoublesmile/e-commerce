package com.example.customerservice.client;

import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewClient {

    Flux<Review> findAllReviewByProductId(Long productId);

    Mono<Review> addReview(Long productId, CreateReviewDto dto);
}
