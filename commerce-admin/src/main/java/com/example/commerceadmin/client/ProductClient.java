package com.example.commerceadmin.client;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;

import java.util.List;

public interface ProductClient {
    List<Product> findAll(String filter);

    Product findById(Long id);

    Product save(CreateProductDto productDto);

    void update(Long id, UpdateProductDto productDto);

    void delete(Long id);
}
