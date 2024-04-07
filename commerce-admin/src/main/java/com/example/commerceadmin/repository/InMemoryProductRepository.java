package com.example.commerceadmin.repository;

import com.example.commerceadmin.model.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    private final List<Product> productList = new CopyOnWriteArrayList<>();

    public InMemoryProductRepository() {
        productList.addAll(getDefaultProductList());
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productList);
    }

    private List<Product> getDefaultProductList() {
        List<Product> data = new ArrayList<>();
        data.add(new Product(1L, "Milk", "milk description"));
        data.add(new Product(2L, "Meat", "meat description"));
        data.add(new Product(3L, "Cheese", "cheese description"));
        data.add(new Product(4L, "Water", "water description"));
        return data;
    }
}
