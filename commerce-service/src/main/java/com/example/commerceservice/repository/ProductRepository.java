package com.example.commerceservice.repository;


import com.example.commerceservice.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();

    Optional<Product> save(Product product);

    Optional<Product> findById(Long id);

    void delete(Long id);

}
