package com.example.feedbackservice.controller;

import com.example.feedbackservice.model.dto.CreateFavouriteDto;
import com.example.feedbackservice.model.entity.Favourite;
import com.example.feedbackservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public Flux<Favourite> findAll(Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono.flatMapMany(tokenHolder ->
                favouriteService.findAll(tokenHolder.getToken().getSubject()));
    }

    @GetMapping("/by-product/{productId}")
    public Mono<Favourite> findByProductId(
            @PathVariable("productId") Long productId,
            Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono.flatMap(tokenHolder ->
                favouriteService.findByProductId(productId, tokenHolder.getToken().getSubject()));
    }

    @PostMapping
    public Mono<ResponseEntity<Favourite>> addByProductId(
            @Validated @RequestBody Mono<CreateFavouriteDto> dtoMono,
            Mono<JwtAuthenticationToken> tokenMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return Mono.zip(tokenMono, dtoMono)
                .flatMap(tuple -> favouriteService.add(tuple.getT2().productId(), tuple.getT1().getToken().getSubject()))
                .map(favourite -> ResponseEntity.created(uriComponentsBuilder.replacePath("/api/v1/favourites/{id}")
                                .build(favourite.getId()))
                        .body(favourite));
    }

    @DeleteMapping("/by-product/{productId}")
    public Mono<ResponseEntity<Void>> removeByProductId(
            @PathVariable("productId") Long productId,
            Mono<JwtAuthenticationToken> tokenMono) {
        return tokenMono.flatMap(tokenHolder ->
                favouriteService.remove(productId, tokenHolder.getToken().getSubject())
                        .then(Mono.just(ResponseEntity.noContent().build())));
    }
}
