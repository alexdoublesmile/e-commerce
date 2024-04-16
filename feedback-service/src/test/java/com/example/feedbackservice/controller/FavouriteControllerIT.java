package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.entity.Favourite;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@Slf4j
@SpringBootTest
@AutoConfigureWebTestClient
class FavouriteControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                new Favourite(UUID.fromString("37b79df0-cbda-11ee-b5d0-17231cdeab05"), 2L,
                        "3c467d3c-cbda-11ee-aa43-1782cd18c42f"),
                new Favourite(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(Favourite.class).all().block();
    }

    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .get()
                .uri("/api/v1/favourites")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        [
                            {
                                "id": "fe87eef6-cbd7-11ee-aeb6-275dac91de02",
                                "productId": 1,
                                "userId": "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"
                            },
                            {
                                "id": "23ff1d58-cbd8-11ee-9f4f-ef497a4e4799",
                                "productId": 3,
                                "userId": "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"
                            }
                        ]""");
    }

    @Test
    void findFavouriteProducts_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // given

        // when
        this.webTestClient
                .get()
                .uri("/api/v1/favourites")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void findFavouriteProductByProductId_ReturnsFavouriteProduct() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .get()
                .uri("/api/v1/favourites/by-product/3")
                .exchange()
                // then
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                            "id": "23ff1d58-cbd8-11ee-9f4f-ef497a4e4799",
                            "productId": 3,
                            "userId": "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"
                        }""");
    }

    @Test
    void findFavouriteProductByProductId_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // given

        // when
        this.webTestClient
                .get()
                .uri("/api/v1/favourites/by-product/3")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void addFavouriteProduct_RequestIsValid_ReturnsCreatedFavouriteProduct() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .post()
                .uri("/api/v1/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 4
                        }""")
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                            "productId": 4,
                            "userId": "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"
                        }""").jsonPath("$.id").exists();
    }

    @Test
    void addFavouriteProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .post()
                .uri("/api/v1/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": null
                        }""")
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody().json("""
                        {
                            "errors": ["must not be null"]
                        }""");
    }

    @Test
    void addFavouriteProduct_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // given

        // when
        this.webTestClient
                .post()
                .uri("/api/v1/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": null
                        }""")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        // given

        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .delete()
                .uri("/api/v1/favourites/by-product/1")
                .exchange()
                // then
                .expectStatus().isNoContent();
    }

    @Test
    void removeProductFromFavourites_UserIsNotAuthenticated_ReturnsUnauthorized() {
        // given

        // when
        this.webTestClient
                .delete()
                .uri("/api/v1/favourites/by-product/1")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}