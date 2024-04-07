package com.example.commerceadmin.service;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import com.example.commerceadmin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

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

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s", id)));
    }

    public Product update(Long id, UpdateProductDto productDto) {
        final Product productFromDB = productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s for update", id)));
        productFromDB.setTitle(productDto.getTitle());
        productFromDB.setDetails(productDto.getDetails());

        return productFromDB;
    }

    public void delete(Long id) {
        productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s for delete", id)));
        productRepository.delete(id);
    }
}
