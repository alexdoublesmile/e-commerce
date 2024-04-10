package com.example.commerceservice.service;

import com.example.commerceservice.model.dto.CreateProductDto;
import com.example.commerceservice.model.dto.UpdateProductDto;
import com.example.commerceservice.model.entity.Product;
import com.example.commerceservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService service;

    @Test
    void findAllProducts_FilterIsNotSet_ReturnsProductsList() {
        // given
        final List<Product> productList = LongStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i), "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(productList).when(productRepository).findAll();

        // when
        var result = service.findAll(null);

        // then
        assertEquals(productList, result);

        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findAllProducts_FilterIsSet_ReturnsFilteredProductsList() {
        // given
        var products = LongStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i), "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productRepository).findAllByFilter("%товар%");

        // when
        var result = service.findAll("товар");

        // then
        assertEquals(products, result);

        verify(productRepository).findAllByFilter("%товар%");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void findProduct_ProductExists_ReturnsNotEmptyOptional() {
        // given
        var product = new Product(1L, "Товар №1", "Описание товара №1");

        doReturn(Optional.of(product)).when(productRepository).findById(1L);

        // when
        var result = service.findById(1L);

        // then
        assertNotNull(result);
        assertEquals(product, result);

        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void createProduct_ReturnsCreatedProduct() {
        // given
        var title = "Новый товар";
        var details = "Описание нового товара";
        final CreateProductDto productDto = new CreateProductDto(title, details);
        final Product transientProduct = new Product(null, title, details);
        final Product product = new Product(1L, title, details);

        doReturn(product)
                .when(productRepository).save(transientProduct);

        // when
        var result = service.save(productDto);

        // then
        assertEquals(product, result);

        verify(productRepository).save(transientProduct);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProduct_ProductExists_UpdatesProduct() {
        // given
        var productId = 1L;
        var product = new Product(1L, "Новый товар", "Описание нового товара");
        var title = "Новое название";
        var details = "Новое описание";
        final UpdateProductDto productDto = new UpdateProductDto(title, details);

        doReturn(Optional.of(product))
                .when(productRepository).findById(1L);

        // when
        service.update(productId, productDto);

        // then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given
        var productId = 1L;
        var title = "Новое название";
        var details = "Новое описание";
        final UpdateProductDto productDto = new UpdateProductDto(title, details);

        // when
        assertThrows(NoSuchElementException.class, () -> service
                .update(productId, productDto));

        // then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        // given
        Long productId = 1L;

        // when
        assertThrows(NoSuchElementException.class, () -> service.delete(productId));

        // then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }
}