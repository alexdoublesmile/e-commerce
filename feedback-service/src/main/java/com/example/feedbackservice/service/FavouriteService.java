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

    public Mono<Favourite> add(Long id) {
        return favouriteRepository.save(new Favourite(UUID.randomUUID(), id));
    }

    public Mono<Void> remove(Long id) {
        return favouriteRepository.deleteByProductId(id);
    }

    public Mono<Favourite> findByProductId(Long productId) {
        return favouriteRepository.findByProductId(productId);
    }

    public Flux<Favourite> findAll() {
        return favouriteRepository.findAll();
    }
}
