package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateReviewDto;
import com.example.feedbackservice.model.entity.Review;
import com.example.feedbackservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Flux<Review> findAll(@RequestParam Long productId) {
        return reviewService.findAllByProductId(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<Review>> addReview(
            @RequestParam Long productId,
            @Validated @RequestBody Mono<CreateReviewDto> dto,
            UriComponentsBuilder uriComponentsBuilder) {
        return dto
                .flatMap(payload -> reviewService.add(productId, payload))
                .map(review -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/api/v1/reviews/{id}")
                                .build(review.getId()))
                        .body(review));
    }
}
