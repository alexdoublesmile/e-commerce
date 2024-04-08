package com.example.commerceservice.repository;


import com.example.commerceservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByTitleLikeIgnoreCase(String filter);
}
