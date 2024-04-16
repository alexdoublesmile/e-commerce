package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateFavouriteDto;
import com.example.feedbackservice.model.entity.Favourite;
import com.example.feedbackservice.service.FavouriteService;
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
class FavouriteControllerTest {

    @Mock
    FavouriteService favouriteService;

    @InjectMocks
    FavouriteController controller;

    @Test
    void findFavourites_ReturnsFavourites() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                new Favourite(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        ))).when(favouriteService).findAll("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.findAll(
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()))))
                // then
                .expectNext(
                        new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                        new Favourite(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3L,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                )
                .verifyComplete();

        verify(favouriteService).findAll("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteService);
    }

    @Test
    void findFavouritesByProductId_ReturnsFavourites() {
        // given
        doReturn(Mono.just(
                new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        )).when(favouriteService).findByProductId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.findByProductId(1L,
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()))))
                // then
                .expectNext(new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                .verifyComplete();

        verify(favouriteService)
                .findByProductId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteService);
    }

    @Test
    void addProductToFavourites_ReturnsCreatedFavourite() {
        // given
        doReturn(Mono.just(new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(favouriteService).add(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.addByProductId(
                        Mono.just(new CreateFavouriteDto(1L)),
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity.created(URI.create("http://localhost/api/v1/favourites/fe87eef6-cbd7-11ee-aeb6-275dac91de02"))
                        .body(new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                                1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .verifyComplete();

        verify(favouriteService).add(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(favouriteService);
    }

    @Test
    void removeProductFromFavourites_ReturnsNoContent() {
        // given
        doReturn(Mono.empty()).when(favouriteService)
                .remove(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(controller.removeByProductId(1L, Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                        .headers(headers -> headers.put("foo", "bar"))
                        .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build()))))
                // then
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();

        verify(favouriteService)
                .remove(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
    }
}