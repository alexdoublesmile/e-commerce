package com.example.customerservice.controller;

import com.example.customerservice.client.FavouriteClient;
import com.example.customerservice.client.ProductClient;
import com.example.customerservice.client.ReviewClient;
import com.example.customerservice.exception.ClientBadRequestException;
import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Favourite;
import com.example.customerservice.model.entity.Product;
import com.example.customerservice.model.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductClient productClient;

    @Mock
    FavouriteClient favouriteClient;

    @Mock
    ReviewClient reviewClient;

    @InjectMocks
    ProductController controller;

    @Test
    void getProductPage_ReturnsProductPage() {
        // given
        var model = new ConcurrentModel();
        var productReviews = List.of(
                new Review(UUID.fromString("6a8512d8-cbaa-11ee-b986-376cc5867cf5"), 1L, 5, "На пятёрочку", "testUser"),
                new Review(UUID.fromString("849c3fac-cbaa-11ee-af68-737c6d37214a"), 1L, 4, "Могло быть и лучше", "testUser"));

        doReturn(Flux.fromIterable(productReviews)).when(reviewClient).findAllReviewByProductId(1L);

        var favouriteProduct = new Favourite(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1L);
        doReturn(Mono.just(favouriteProduct)).when(favouriteClient).findFavouriteByProductId(1L);

        var product = new Product(1L, "title", "description");
        doReturn(Mono.just(product)).when(productClient).findById(1L);

        // when
        StepVerifier.create(controller.getProductPage(1L, model))
                // then
                .expectNext("product/item")
                .verifyComplete();

        assertEquals(product, model.getAttribute("product"));
        assertEquals(productReviews, model.getAttribute("reviewList"));
        assertEquals(true, model.getAttribute("isFavourite"));

        verify(productClient).findById(1L);
        verify(reviewClient).findAllReviewByProductId(1L);
        verify(favouriteClient).findFavouriteByProductId(1L);
        verifyNoMoreInteractions(productClient, favouriteClient);
    }

    @Test
    void addProductToFavourites_RequestIsValid_RedirectsToProductPage() {
        // given
        doReturn(Mono.just(new Favourite(UUID.fromString("25ec67b4-cbac-11ee-adc8-4bd80e8171c4"), 1L)))
                .when(favouriteClient).addFavourite(1L);

        // when
        StepVerifier.create(controller.addToFavourite(1L))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteClient).addFavourite(1L);
        verifyNoMoreInteractions(favouriteClient);
        verifyNoInteractions(reviewClient, productClient);
    }

    @Test
    void addProductToFavourites_RequestIsInvalid_RedirectsToProductPage() {
        // given
        doReturn(Mono.error(new ClientBadRequestException(null,
                List.of("Какая-то ошибка"))))
                .when(favouriteClient).addFavourite(1L);

        // when
        StepVerifier.create(controller.addToFavourite(1L))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteClient).addFavourite(1L);
        verifyNoMoreInteractions(favouriteClient);
        verifyNoInteractions(reviewClient, productClient);
    }

    @Test
    void removeProductFromFavourites_RedirectsToProductPage() {
        // given
        doReturn(Mono.empty()).when(favouriteClient).removeFavourite(1L);

        // when
        StepVerifier.create(controller.removeFromFavourite(1L))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        verify(favouriteClient).removeFavourite(1L);
        verifyNoMoreInteractions(favouriteClient);
        verifyNoInteractions(productClient, reviewClient);
    }

    @Test
    void createReview_RequestIsValid_RedirectsToProductPage() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        doReturn(Mono.just(new Review(UUID.fromString("86efa22c-cbae-11ee-ab01-679baf165fb7"), 1L, 3, "Ну, на троечку...", "testUser")))
                .when(reviewClient).addReview(1L, new CreateReviewDto(3, "Ну, на троечку..."));

        // when
        StepVerifier.create(controller.addReview(1L,
                        new CreateReviewDto(3, "Ну, на троечку..."), model))
                // then
                .expectNext("redirect:/customer/products/1")
                .verifyComplete();

        assertNull(response.getStatusCode());

        verify(reviewClient).addReview(1L, new CreateReviewDto(3, "Ну, на троечку..."));
        verifyNoMoreInteractions(reviewClient);
        verifyNoInteractions(productClient, favouriteClient);
    }

    @Test
    void createReview_RequestIsInvalid_ReturnsProductPageWithPayloadAndErrors() {
        // given
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        var favouriteProduct = new Favourite(UUID.fromString("af5f9496-cbaa-11ee-a407-27b46917819e"), 1L);
        doReturn(Mono.just(favouriteProduct)).when(favouriteClient).findFavouriteByProductId(1L);

        doReturn(Mono.error(new ClientBadRequestException(null, List.of("Ошибка 1L", "Ошибка 2"))))
                .when(reviewClient).addReview(1L, new CreateReviewDto(null, "Очень длинный отзыв"));

        doReturn(Flux.empty()).when(reviewClient).findAllReviewByProductId(1L);

        var product = new Product(1L, "title", "description");
        doReturn(Mono.just(product)).when(productClient).findById(1L);

        // when
        StepVerifier.create(controller.addReview(1L,
                        new CreateReviewDto(null, "Очень длинный отзыв"), model))
                // then
                .expectNext("product/item")
                .verifyComplete();

        assertEquals(product, model.getAttribute("product"));
        assertEquals(true, model.getAttribute("isFavourite"));
        assertEquals(new CreateReviewDto(null, "Очень длинный отзыв"), model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1L", "Ошибка 2"), model.getAttribute("errors"));

        verify(reviewClient).addReview(1L, new CreateReviewDto(null, "Очень длинный отзыв"));
        verify(favouriteClient).findFavouriteByProductId(1L);
        verify(productClient).findById(1L);
        verifyNoMoreInteractions(reviewClient, favouriteClient);
    }

    @Test
    @DisplayName("Исключение NoSuchElementException должно быть транслировано в страницу errors/404")
    void handleNoSuchElementException_ReturnsErrors404() {
        // given
        var exception = new NoSuchElementException("Товар не найден");
        var model = new ConcurrentModel();
        var response = new MockServerHttpResponse();

        // when
        var result = controller.handleNoSuchElementException(exception, model, response);

        // then
        assertEquals("error/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProductsListPage_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new Product(1L, "Отфильтрованный товар №1L", "Описание отфильтрованного товара №1L"),
                new Product(2L, "Отфильтрованный товар №2", "Описание отфильтрованного товара №2"),
                new Product(3L, "Отфильтрованный товар №3", "Описание отфильтрованного товара №3")
        ))).when(productClient).findAll("фильтр");

        // when
        StepVerifier.create(controller.getProductListPage("фильтр", model))
                // then
                .expectNext("product/list")
                .verifyComplete();

        assertEquals("фильтр", model.getAttribute("filter"));
        assertEquals(List.of(
                        new Product(1L, "Отфильтрованный товар №1L", "Описание отфильтрованного товара №1L"),
                        new Product(2L, "Отфильтрованный товар №2", "Описание отфильтрованного товара №2"),
                        new Product(3L, "Отфильтрованный товар №3", "Описание отфильтрованного товара №3")),
                model.getAttribute("productList"));

        verify(productClient).findAll("фильтр");
        verifyNoMoreInteractions(productClient);
        verifyNoInteractions(favouriteClient);
    }

    @Test
    void getFavouritesPage_ReturnsFavouritesPage() {
        // given
        var model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new Product(1L, "Отфильтрованный товар №1L", "Описание отфильтрованного товара №1L"),
                new Product(2L, "Отфильтрованный товар №2", "Описание отфильтрованного товара №2"),
                new Product(3L, "Отфильтрованный товар №3", "Описание отфильтрованного товара №3")
        ))).when(productClient).findAll("фильтр");

        doReturn(Flux.fromIterable(List.of(
                new Favourite(UUID.fromString("a16f0218-cbaf-11ee-9e6c-6b0fa3631587"), 1L),
                new Favourite(UUID.fromString("a42ff37c-cbaf-11ee-8b1d-cb00912914b5"), 3L)
        ))).when(favouriteClient).findAllFavourite();

        // when
        StepVerifier.create(controller.getFavouriteListPage("фильтр", model))
                // then
                .expectNext("product/favourites")
                .verifyComplete();

        assertEquals("фильтр", model.getAttribute("filter"));
        assertEquals(List.of(
                        new Product(1L, "Отфильтрованный товар №1L", "Описание отфильтрованного товара №1L"),
                        new Product(3L, "Отфильтрованный товар №3", "Описание отфильтрованного товара №3")),
                model.getAttribute("productList"));

        verify(productClient).findAll("фильтр");
        verify(favouriteClient).findAllFavourite();
        verifyNoMoreInteractions(productClient, favouriteClient);

    }
}