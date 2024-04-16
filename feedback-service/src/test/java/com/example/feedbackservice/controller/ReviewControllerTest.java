package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateReviewDto;
import com.example.feedbackservice.model.entity.Review;
import com.example.feedbackservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    ReviewService reviewService;

    @InjectMocks
    ReviewController controller;

    @Test
    void findReviewsByProductId_ReturnsReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new Review(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1L, 1,
                        "Отзыв №1", "user-1"),
                new Review(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1L, 3,
                        "Отзыв №2", "user-2"),
                new Review(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1L, 5,
                        "Отзыв №3", "user-3")
        ))).when(reviewService).findAllByProductId(1L);

        // when
        StepVerifier.create(controller.findAll(1L))
                // then
                .expectNext(
                        new Review(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1L, 1,
                                "Отзыв №1", "user-1"),
                        new Review(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1L, 3,
                                "Отзыв №2", "user-2"),
                        new Review(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1L, 5,
                                "Отзыв №3", "user-3")
                )
                .verifyComplete();

        verify(reviewService).findAllByProductId(1L);
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    void createReview_ReturnsCreatedReview() {
        // given
        doReturn(Mono.just(new Review(UUID.fromString("5a9ba234-cbd6-11ee-acab-5748ca6678b9"), 1L, 4,
                "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(reviewService)
                .add(1L, 4, "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.addReview(1L,
                        Mono.just(new CreateReviewDto(4, "В целом норм")),
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity.created(URI.create("http://localhost/api/v1/reviews/5a9ba234-cbd6-11ee-acab-5748ca6678b9"))
                        .body(new Review(UUID.fromString("5a9ba234-cbd6-11ee-acab-5748ca6678b9"), 1L, 4,
                                "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .verifyComplete();

        verify(reviewService)
                .add(1L, 4, "В целом норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(reviewService);
    }
}