package com.example.customerservice.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;


@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        stubFor(get("/api/v1/products/1")
                .willReturn(okJson("""
                        {
                            "id": 1,
                            "title": "Название товара №1",
                            "details": "Описание товара №1"
                        }""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        stubFor(get(urlPathMatching("/api/v1/products"))
                .withQueryParam("filter", equalTo("фильтр"))
                .willReturn(okJson("""
                        [
                            {
                                "id": 1,
                                "title": "Отфильтрованный товар №1",
                                "details": "Описание отфильтрованного товара №1"
                            },
                            {
                                "id": 2,
                                "title": "Отфильтрованный товар №2",
                                "details": "Описание отфильтрованного товара №2"
                            },
                            {
                                "id": 3,
                                "title": "Отфильтрованный товар №3",
                                "details": "Описание отфильтрованного товара №3"
                            }
                        ]""")));
    }

    // TODO: 16.04.2024  fix test
//    @Test
    void getProductPage_ProductExists_ReturnsProductPage() {
        // given
        stubFor(get("/api/v1/reviews/by-product/1")
                .willReturn(okJson("""
                        [
                            {
                                "id": "595d4e5a-cbc1-11ee-864f-8fb72674ccaf",
                                "productId": 1,
                                "rating": 3,
                                "review": "Ну, на троечку...",
                                "userId": "5da9bf2a-cbc1-11ee-a8a7-d355f5a3dd8e"
                            },
                            {
                                "id": "63c4410a-cbc1-11ee-92ea-eff590e7852e",
                                "productId": 1,
                                "rating": 5,
                                "review": "Отличный товар!",
                                "userId": "6b3cce0c-cbc1-11ee-ac61-b7eed6e7b4f4"
                            }
                        ]""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        stubFor(get("/api/v1/products/by-product/1")
                .willReturn(created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "2ecc74c2-cb17-11ee-b719-e35a0e241f11",
                                    "productId": 1
                                }""")));

        // when
        webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/1")
                .exchange()
                // then
                .expectStatus().isOk();

        verify(getRequestedFor(urlPathMatching("/api/v1/products/1")));
        verify(getRequestedFor(urlPathMatching("/api/v1/reviews/by-product/1")));
        verify(getRequestedFor(urlPathMatching("/api/v1/products/by-product/1")));

    }

    @Test
    void getProductPage_ProductDoesNotExist_ReturnsNotFound() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/404")
                .exchange()
                // then
                .expectStatus().isNotFound();

        // TODO: 16.04.2024 add verify
//        verify(getRequestedFor(urlPathMatching("/api/v1/products/404")));
    }

    @Test
    void getProductPage_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .get()
                .uri("/customer/products/1")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void addProductToFavourites_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        stubFor(post("/api/v1/favourites")
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1
                        }"""))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "2ecc74c2-cb17-11ee-b719-e35a0e241f11",
                                    "productId": 1
                                }""")));

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        // TODO: 16.04.2024 add verify
//        verify(getRequestedFor(urlPathMatching("/api/v1/products/1")));
//        verify(postRequestedFor(urlPathMatching("/api/v1/favourites"))
//                .withRequestBody(equalToJson("""
//                        {
//                            "productId": 1
//                        }""")));
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void addProductToFavourites_ProductDoesNotExist_ReturnsNotFoundPage() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/404/add-to-favourites")
                .exchange()
                // then
                .expectStatus().isNotFound();

        verify(getRequestedFor(urlPathMatching("/api/v1/products/404")));
    }

    @Test
    void addProductToFavourites_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void removeProductFromFavourites_ProductExists_ReturnsRedirectionToProductPage() {
        // given
        stubFor(delete("/api/v1/products/by-product/1")
                .willReturn(noContent()));

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/remove-from-favourites")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        verify(getRequestedFor(urlPathMatching("/api/v1/products/1")));
        verify(deleteRequestedFor(urlPathMatching("/api/v1/products/by-product/1")));
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void removeProductFromFavourites_ProductDoesNotExist_ReturnsNotFoundPage() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/404/remove-from-favourites")
                .exchange()
                // then
                .expectStatus().isNotFound();

        verify(getRequestedFor(urlPathMatching("/api/v1/favourites/by-product/404")));
    }

    @Test
    void removeProductFromFavourites_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/remove-from-favourites")
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        stubFor(post("/api/v1/reviews")
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Ну, на троечку..."
                        }"""))
                .willReturn(created()
                        .withHeader(HttpHeaders.LOCATION, "http://localhost/api/v1/reviews/b852bc8e-cbc5-11ee-bbc5-bf192e2492e5")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": "b852bc8e-cbc5-11ee-bbc5-bf192e2492e5",
                                    "productId": 1,
                                    "rating": 3,
                                    "review": "Ну, на троечку...",
                                    "userId": "1a24d4ec-cbc6-11ee-af3b-0b236022162c"
                                }""")));

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-review")
                .body(BodyInserters.fromFormData("rating", "3")
                        .with("review", "Ну, на троечку..."))
                // then
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        verify(postRequestedFor(urlPathMatching("/api/v1/reviews"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Ну, на троечку..."
                        }""")));
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void createReview_RequestIsInvalid_ReturnsProductPage() throws Exception {
        // given
        stubFor(post("/api/v1/reviews")
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": -1,
                            "review": "Ну очень длинный отзыв (да, тут более 1000 символов)"
                        }"""))
                .willReturn(badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

        stubFor(get("/api/v1/products/by-product/1")
                .willReturn(okJson("""
                        {
                            "id": "ec586ecc-cbc8-11ee-8e7d-4fce5e860855",
                            "productId": 1,
                            "userId": "f1177a8e-cbc8-11ee-8ca2-0bf025125fd5"
                        }""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-review")
                .body(BodyInserters.fromFormData("rating", "-1")
                        .with("review", "Ну очень длинный отзыв (да, тут более 1000 символов)"))
                // then
                .exchange()
                .expectStatus().isBadRequest();

        verify(postRequestedFor(urlPathMatching("/api/v1/reviews"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""
                        {
                            "productId": 1,
                            "rating": -1,
                            "review": "Ну очень длинный отзыв (да, тут более 1000 символов)"
                        }""")));
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void createReview_ProductDoesNotExist_ReturnsNotFoundPage() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/404/add-review")
                .body(BodyInserters.fromFormData("rating", "3")
                        .with("review", "Ну, на троечку..."))
                .exchange()
                // then
                .expectStatus().isNotFound();

        // TODO: 16.04.2024 add verify
//        verify(getRequestedFor(urlPathMatching("/api/v1/products/404")));
    }

    @Test
    void createReview_UserIsNotAuthorized_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-review")
                .body(BodyInserters.fromFormData("rating", "3")
                        .with("review", "Ну, на троечку..."))
                .exchange()
                // then
                .expectStatus().isFound()
                .expectHeader().location("/login");
    }

    @Test
    void getProductsListPage_ReturnsProductsPage() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/list?filter=фильтр")
                .exchange()
                // then
                .expectStatus().isOk();

        // TODO: 16.04.2024 add verify
//        verify(getRequestedFor(urlPathMatching("/api/v1/products"))
//                .withQueryParam("filter", equalTo("фильтр")));
    }

    @Test
    void getProductsListPage_UserIsNotAuthenticated_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .get()
                .uri("/customer/products/list")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    // TODO: 16.04.2024 fix test
//    @Test
    void getFavouriteProductsPage_ReturnsFavouriteProductsPage() {
        // given
        stubFor(get("/api/v1/products")
                .willReturn(okJson("""
                        [
                            {
                                "id": "a16f0218-cbaf-11ee-9e6c-6b0fa3631587",
                                "productId": 1,
                                "userId": "2051e72a-cbca-11ee-8e8b-a3841adf45d0"
                            },
                            {
                                "id": "a42ff37c-cbaf-11ee-8b1d-cb00912914b5",
                                "productId": 3,
                                "userId": "2051e72a-cbca-11ee-8e8b-a3841adf45d0"
                            }
                        ]""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        webTestClient
                .mutateWith(mockUser())
                .get()
                .uri("/customer/products/favourites?filter=фильтр")
                .exchange()
                // then
                .expectStatus().isOk();

        verify(getRequestedFor(urlPathMatching("/api/v1/products"))
                .withQueryParam("filter", equalTo("фильтр")));
        verify(getRequestedFor(urlPathMatching("/api/v1/products")));
    }

    @Test
    void getFavouriteProductsPage_UserIsNotAuthenticated_RedirectsToLoginPage() {
        // given

        // when
        webTestClient
                .get()
                .uri("/customer/products/favourites")
                .exchange()
                // then
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }
}