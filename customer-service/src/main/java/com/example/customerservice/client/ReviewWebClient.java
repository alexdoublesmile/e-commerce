package com.example.customerservice.client;

import com.example.customerservice.exception.ClientBadRequestException;
import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Favourite;
import com.example.customerservice.model.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewWebClient implements ReviewClient {

    private final WebClient webClient;

    @Override
    public Flux<Review> findAllReviewByProductId(Long productId) {
        return webClient
                .get()
                .uri(builder ->
                    builder.path("/api/v1/reviews")
                            .queryParam("productId", productId)
                            .build())
                .retrieve()
                .bodyToFlux(Review.class);
    }

    @Override
    public Mono<Review> addReview(Long productId, CreateReviewDto dto) {
        return webClient
                .post()
                .uri(builder ->
                        builder.path("/api/v1/reviews")
                                .queryParam("productId", productId)
                                .build())
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Review.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException(ex,
                                ((List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))));
    }
}
