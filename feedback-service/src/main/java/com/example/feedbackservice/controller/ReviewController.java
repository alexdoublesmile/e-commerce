package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateReviewDto;
import com.example.feedbackservice.model.entity.Review;
import com.example.feedbackservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            Mono<JwtAuthenticationToken> tokenMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return tokenMono
                .flatMap(tokenHolder -> dto
                        .flatMap(payload -> reviewService.add(
                                productId,
                                payload.rating(),
                                payload.text(),
                                tokenHolder.getToken().getSubject()))
                        .map(review -> ResponseEntity
                                .created(uriComponentsBuilder.replacePath("/api/v1/reviews/{id}").build(review.getId()))
                                .body(review)));
    }
}
