package com.example.commerceservice.service;

import com.example.commerceservice.model.dto.CreateProductDto;
import com.example.commerceservice.model.dto.UpdateProductDto;
import com.example.commerceservice.model.entity.Product;
import com.example.commerceservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

// TODO: 08.04.2024 norm add mapping
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll(String filter) {
        System.out.println(filter);
        return filter == null || filter.isBlank()
                ? productRepository.findAll()
                : productRepository.findAllByFilter("%" + filter + "%");
    }

    @Transactional
    public Product save(CreateProductDto productDto) {
        return productRepository.save(
                new Product(null, productDto.getTitle(), productDto.getDetails()));
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s", id)));
    }

    // TODO: 08.04.2024 make norm patch
    @Transactional
    public Product update(Long id, UpdateProductDto productDto) {
        final Product productFromDB = productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s for update", id)));
        productFromDB.setTitle(productDto.getTitle());
        productFromDB.setDetails(productDto.getDetails());

        return productFromDB;
    }

    @Transactional
    public void delete(Long id) {
        productRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(format(
                        "No product with id %s for delete", id)));
        productRepository.deleteById(id);
    }
}
