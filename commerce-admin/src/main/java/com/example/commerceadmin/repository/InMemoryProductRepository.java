package com.example.commerceadmin.repository;

import com.example.commerceadmin.model.entity.Product;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryProductRepository implements ProductRepository {
    private final List<Product> productList = new CopyOnWriteArrayList<>();
}
