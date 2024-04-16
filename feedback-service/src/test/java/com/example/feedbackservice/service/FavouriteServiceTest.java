package com.example.feedbackservice.service;

import com.example.feedbackservice.model.entity.Favourite;
import com.example.feedbackservice.repository.FavouriteRepository;
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
class FavouriteServiceTest {

    @Mock
    FavouriteRepository favouriteRepository;

    @InjectMocks
    FavouriteService service;

    @Test
    void addProductToFavourites_ReturnsCreatedFavourite() {
        // given
        doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0]))
                .when(favouriteRepository).save(any());

        // when
        StepVerifier.create(service.add(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNextMatches(favouriteProduct -> favouriteProduct.getProductId() == 1 &&
                        favouriteProduct.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                        favouriteProduct.getId() != null)
                .verifyComplete();

        verify(favouriteRepository).save(argThat(favouriteProduct -> favouriteProduct.getProductId() == 1 &&
                favouriteProduct.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                favouriteProduct.getId() != null));
    }

    @Test
    void removeProductFromFavourites_ReturnsEmptyMono() {
        // given
        doReturn(Mono.empty()).when(favouriteRepository)
                .deleteByProductIdAndUserId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.remove(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .verifyComplete();

        verify(favouriteRepository)
                .deleteByProductIdAndUserId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
    }

    @Test
    void findFavouriteByProduct_ReturnsFavourite() {
        // given
        doReturn(Mono.just(new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(favouriteRepository)
                .findByProductIdAndUserId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.findByProductId(1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNext(new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                        1L, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                .verifyComplete();
    }

    @Test
    void findFavourites_ReturnsFavourites() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                new Favourite(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3L,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        ))).when(favouriteRepository).findAllByUserId("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.findAll("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNext(
                        new Favourite(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1L,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                        new Favourite(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3L,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                )
                .verifyComplete();
    }
}