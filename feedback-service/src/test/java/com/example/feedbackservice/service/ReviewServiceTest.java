package com.example.feedbackservice.service;

import com.example.feedbackservice.model.entity.Review;
import com.example.feedbackservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewService service;

    @Test
    void createReview_ReturnsCreatedReview() {
        // given
        doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0])).when(reviewRepository)
                .save(any());

        // when
        StepVerifier.create(service.add(1L, 3, "Ну, на троечку",
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNextMatches(productReview ->
                        productReview.getProductId() == 1 && productReview.getRating() == 3 &&
                                productReview.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                                productReview.getText().equals("Ну, на троечку") && productReview.getId() != null)
                .verifyComplete();

        verify(reviewRepository)
                .save(argThat(productReview ->
                        productReview.getProductId() == 1 && productReview.getRating() == 3 &&
                                productReview.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                                productReview.getText().equals("Ну, на троечку") && productReview.getId() != null));
    }

    @Test
    void findReviewsByProduct_ReturnsReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new Review(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1L, 1,
                        "Отзыв №1", "user-1"),
                new Review(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1L, 3,
                        "Отзыв №2", "user-2"),
                new Review(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1L, 5,
                        "Отзыв №3", "user-3")
        ))).when(reviewRepository).findAllByProductId(1L);

        // when
        StepVerifier.create(service.findAllByProductId(1L))
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
    }
}