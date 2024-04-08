package com.example.commerceservice.repository;

import com.example.commerceservice.model.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductRepository {
    private final List<Product> productList = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong(0L);

    public InMemoryProductRepository() {
        productList.addAll(getDefaultProductList());
    }

    public List<Product> findAll() {
        return new ArrayList<>(productList);
    }

    public Optional<Product> save(Product product) {
        product.setId(sequence.incrementAndGet());
        productList.add(product);

        return findById(product.getId());
    }

    public Optional<Product> findById(Long id) {
        return productList.stream()
                .dropWhile(product -> !product.getId().equals(id))
                .findFirst();
    }

    public void delete(Long id) {
        productList.removeIf(product -> product.getId().equals(id));
    }

    private List<Product> getDefaultProductList() {
        List<Product> data = new ArrayList<>();
        data.add(new Product(sequence.incrementAndGet(), "Milk", "milk description"));
        data.add(new Product(sequence.incrementAndGet(), "Meat", "meat description"));
        data.add(new Product(sequence.incrementAndGet(), "Cheese", "cheese description"));
        data.add(new Product(sequence.incrementAndGet(), "Water", "water description"));
        return data;
    }
}
