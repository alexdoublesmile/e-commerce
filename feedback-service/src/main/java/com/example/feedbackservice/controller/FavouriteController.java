package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateFavouriteDto;
import com.example.feedbackservice.model.entity.Favourite;
import com.example.feedbackservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @GetMapping
    public Flux<Favourite> findAll() {
        return favouriteService.findAll();
    }

    @GetMapping("/by-product/{productId}")
    public Mono<Favourite> findByProductId(@PathVariable("productId") Long productId) {
        return favouriteService.findByProductId(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<Favourite>> addByProductId(
            @Validated @RequestBody Mono<CreateFavouriteDto> dto,
            UriComponentsBuilder uriComponentsBuilder) {
        return dto
                .flatMap(payload -> favouriteService.add(payload.productId()))
                .map(favourite -> ResponseEntity.created(uriComponentsBuilder.replacePath("/api/v1/favourites/{id}")
                                .build(favourite.getId()))
                        .body(favourite));
    }

    @DeleteMapping("/by-product/{productId}")
    public Mono<ResponseEntity<Void>> removeByProductId(@PathVariable("productId") Long productId) {
        return favouriteService.remove(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
