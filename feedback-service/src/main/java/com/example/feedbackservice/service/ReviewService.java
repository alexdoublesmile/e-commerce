package com.example.feedbackservice.service;

import com.example.feedbackservice.model.dto.CreateReviewDto;
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

    public Mono<Review> add(Long productId, CreateReviewDto dto) {
        return reviewRepository.save(
                new Review(UUID.randomUUID(), productId, dto.rating(), dto.text()));
    }

    public Flux<Review> findAllByProductId(Long productId) {
        return reviewRepository.findAllByProductId(productId);
    }
}
