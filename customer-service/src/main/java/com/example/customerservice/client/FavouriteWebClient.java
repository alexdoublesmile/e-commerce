package com.example.customerservice.client;

import com.example.customerservice.exception.ClientBadRequestException;
import com.example.customerservice.model.dto.CreateFavouriteDto;
import com.example.customerservice.model.entity.Favourite;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class FavouriteWebClient implements FavouriteClient {

    private final WebClient webClient;

    @Override
    public Flux<Favourite> findAllFavourite() {
        log.info("Request...");
        final Flux<Favourite> response = webClient
                .get()
                .uri("/api/v1/favourites")
                .retrieve()
                .bodyToFlux(Favourite.class).log();
        log.info("Response: {}", response);
        return response;
    }

    @Override
    public Mono<Favourite> findFavouriteByProductId(Long productId) {
        return webClient
                .get()
                .uri("/api/v1/favourites/by-product/{productId}", productId)
                .retrieve()
                .bodyToMono(Favourite.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<Favourite> addFavourite(Long id) {
        return webClient
                .post()
                .uri("/api/v1/favourites")
                .bodyValue(new CreateFavouriteDto(id))
                .retrieve()
                .bodyToMono(Favourite.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException(ex,
                                ((List<String>) ex.getResponseBodyAs(ProblemDetail.class).getProperties().get("errors"))));
    }

    @Override
    public Mono<Void> removeFavourite(Long id) {
        return webClient
                .delete()
                .uri("/api/v1/favourites/by-product/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
