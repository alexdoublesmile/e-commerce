package com.example.commerceservice.repository;


import com.example.commerceservice.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product WHERE title ILIKE :filter", nativeQuery = true)
    List<Product> findAllByFilter(@Param("filter") String filter);
}
