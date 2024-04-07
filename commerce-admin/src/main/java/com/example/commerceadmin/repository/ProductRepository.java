package com.example.commerceadmin.repository;

import com.example.commerceadmin.model.entity.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> findAll();
}
