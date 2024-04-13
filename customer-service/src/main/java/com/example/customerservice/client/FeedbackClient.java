package com.example.customerservice.client;

import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Favourite;
import com.example.customerservice.model.entity.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FeedbackClient {
    Flux<Favourite> findAllFavourite();

    Mono<Favourite> findFavouriteByProductId(Long productId);

    Flux<Review> findAllReviewByProductId(Long productId);

    Mono<Void> addFavourite(Long id);

    Mono<Void> removeFavourite(Long id);

    Mono<Void> addReview(Long productId, CreateReviewDto dto);
}
