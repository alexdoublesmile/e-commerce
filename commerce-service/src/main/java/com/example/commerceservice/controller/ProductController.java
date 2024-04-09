package com.example.commerceservice.controller;

import com.example.commerceservice.model.dto.CreateProductDto;
import com.example.commerceservice.model.dto.UpdateProductDto;
import com.example.commerceservice.model.entity.Product;
import com.example.commerceservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    // TODO: 08.04.2024 add complex filtration
    // TODO: 08.04.2024 add pagination, sorting etc.
    @GetMapping
    public List<Product> findAll(
            @RequestParam(name = "filter", required = false) String filter) {
        return productService.findAll(filter);
    }

    // TODO: 09.04.2024 return dto type
    @GetMapping("/{id:\\d+}")
    public Product findById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> save(
            @Validated @RequestBody CreateProductDto productDto,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder) throws BindException {

        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException ex) {
                throw ex;
            } else {
                throw new BindException(bindingResult);
            }
        } else {

            Product savedProduct = productService.save(productDto);
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/products/{productId}")
                            .build(Map.of("productId", savedProduct.getId())))
                    .body(savedProduct);
        }
    }

    // TODO: 08.04.2024 return patched entity with not 204
    // TODO: 08.04.2024 add put impl
    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<?> patchUpdate(
            @PathVariable("id") Long id,
            @Validated @RequestBody UpdateProductDto productDto,
            BindingResult bindingResult) throws BindException {

        // TODO: 08.04.2024 move validation to model
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException ex) {
                throw ex;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            productService.update(id, productDto);
            return ResponseEntity.noContent().build();
        }
    }

    // TODO: 08.04.2024 return smth
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
