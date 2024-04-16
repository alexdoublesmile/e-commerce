package com.example.feedbackservice.service;

import com.example.feedbackservice.model.entity.Favourite;
import com.example.feedbackservice.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;

    public Mono<Favourite> add(Long productId, String userId) {
        return favouriteRepository.save(new Favourite(UUID.randomUUID(), productId, userId));
    }

    public Mono<Void> remove(Long productId, String userId) {
        return favouriteRepository.deleteByProductIdAndUserId(productId, userId);
    }

    public Mono<Favourite> findByProductId(Long productId, String userId) {
        return favouriteRepository.findByProductIdAndUserId(productId, userId);
    }

    public Flux<Favourite> findAll(String userId) {
        return favouriteRepository.findAllByUserId(userId);
    }
}
