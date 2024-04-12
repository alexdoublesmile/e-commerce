package com.example.customerservice.service;

import com.example.customerservice.model.entity.FavouriteProduct;
import com.example.customerservice.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;

    public Mono<FavouriteProduct> add(Long id) {
        return favouriteRepository.save(new FavouriteProduct(UUID.randomUUID(), id));
    }

    public Mono<Void> remove(Long id) {
        return favouriteRepository.deleteByProductId(id);
    }

    public Mono<FavouriteProduct> findByProductId(Long productId) {
        return favouriteRepository.findByProductId(productId);
    }
}
