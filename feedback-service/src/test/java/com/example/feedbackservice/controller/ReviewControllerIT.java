package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.entity.Review;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class ReviewControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate.insertAll(List.of(
                new Review(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1L, 1,
                        "Отзыв №1", "user-1"),
                new Review(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1L, 3,
                        "Отзыв №2", "user-2"),
                new Review(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1L, 5,
                        "Отзыв №3", "user-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.remove(Review.class).all().block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        // given
        // when
        webTestClient.mutateWith(mockJwt())
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("========== REQUEST ==========");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, value) -> log.info("{}: {}", header, value));
                    log.info("======== END REQUEST ========");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri(builder -> builder.path("/api/v1/reviews")
                        .queryParam("productId", 1)
                        .build())
                .exchange()
                // then
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                                "id": "bd7779c2-cb05-11ee-b5f3-df46a1249898",
                                "productId": 1,
                                "rating": 1,
                                "text": "Отзыв №1",
                                "userId": "user-1"
                            },
                            {"id": "be424abc-cb05-11ee-ab16-2b747e61f570", "productId": 1, "rating": 3, "text": "Отзыв №2", "userId": "user-2"},
                            {"id": "be77f95a-cb05-11ee-91a3-1bdc94fa9de4", "productId": 1, "rating": 5, "text": "Отзыв №3", "userId": "user-3"}
                        ]""");
    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // given
        // when
        webTestClient
                .get()
                .uri(builder -> builder.path("/api/v1/reviews")
                        .queryParam("productId", 1)
                        .build())
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        // given
        // when
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri(builder -> builder.path("/api/v1/reviews")
                        .queryParam("productId", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "rating": 5,
                            "text": "На пяторочку!"
                        }""")
                // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "text": "На пяторочку!",
                            "userId": "user-tester"
                        }""").jsonPath("$.id").exists();
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        // given
        // when
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri(builder -> builder.path("/api/v1/reviews")
                        .queryParam("productId", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "rating": -1,
                            "text": "Sed ut perspiciatis, unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt, explicabo. Nemo enim ipsam voluptatem, quia voluptas sit, aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos, qui ratione voluptatem sequi nesciunt, neque porro quisquam est, qui dolorem ipsum, quia dolor sit, amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt, ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit, qui in ea voluptate velit esse, quam nihil molestiae consequatur, vel illum, qui dolorem eum fugiat, quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus, qui blanditiis praesentium voluptatum deleniti atque corrupti, quos dolores et quas molestias excepturi sint, obcaecati cupiditate non provident, similique sunt in culpa, qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio, cumque nihil impedit, quo minus id, quod maxime placeat, facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet, ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."
                        }""")
                // then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors": [
                                "{customer.products.reviews.create.errors.review_is_too_big}",
                                "{customer.products.reviews.create.errors.rating_is_below_min}"
                            ]
                        }""");
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // given

        // when
        webTestClient
                .post()
                .uri(builder -> builder.path("/api/v1/reviews")
                        .queryParam("productId", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "rating": 5,
                            "text": "На пяторочку!"
                        }""")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}