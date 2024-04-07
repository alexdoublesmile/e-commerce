package com.example.commerceadmin.service;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.entity.Product;
import com.example.commerceadmin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(CreateProductDto productDto) {
        return productRepository.save(
                new Product(null, productDto.getTitle(), productDto.getDetails()))
                .orElseThrow();
    }
}
