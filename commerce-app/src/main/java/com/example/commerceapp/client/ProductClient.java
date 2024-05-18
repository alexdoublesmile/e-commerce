package com.example.commerceapp.client;

import com.example.commerceapp.model.dto.CreateProductDto;
import com.example.commerceapp.model.dto.UpdateProductDto;
import com.example.commerceapp.model.entity.Product;

import java.util.List;

public interface ProductClient {
    List<Product> findAll(String filter);

    Product findById(Long id);

    Product save(CreateProductDto productDto);

    void update(Long id, UpdateProductDto productDto);

    void delete(Long id);
}
