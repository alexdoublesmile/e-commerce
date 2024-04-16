package com.example.feedbackservice.service;

import com.example.feedbackservice.model.entity.Review;
import com.example.feedbackservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Mono<Review> add(Long productId, Integer rating, String text, String userId) {
        return reviewRepository.save(
                new Review(UUID.randomUUID(), productId, rating, text, userId));
    }

    public Flux<Review> findAllByProductId(Long productId) {
        return reviewRepository.findAllByProductId(productId);
    }
}
