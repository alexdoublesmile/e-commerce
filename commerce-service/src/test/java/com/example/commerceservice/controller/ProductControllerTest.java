package com.example.commerceservice.controller;

import com.example.commerceservice.exception.GlobalExceptionHandler;
import com.example.commerceservice.model.dto.CreateProductDto;
import com.example.commerceservice.model.dto.UpdateProductDto;
import com.example.commerceservice.model.entity.Product;
import com.example.commerceservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductController controller;

    @Test
    void findProduct_ReturnsProductsList() {
        // given
        var filter = "товар";

        doReturn(List.of(new Product(1L, "Первый товар", "Описание первого товара"),
                new Product(2L, "Второй товар", "Описание второго товара")))
                .when(productService).findAll("товар");

        // when
        var result = controller.findAll(filter);

        // then
        assertEquals(List.of(new Product(1L, "Первый товар", "Описание первого товара"),
                new Product(2L, "Второй товар", "Описание второго товара")), result);
    }

    @Test
    void createProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new CreateProductDto("Новое название", "Новое описание");
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Product(1L, "Новое название", "Новое описание"))
                .when(productService).save(payload);

        // when
        var result = controller.save(payload, bindingResult, uriComponentsBuilder);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/api/v1/products/1"), result.getHeaders().getLocation());
        assertEquals(new Product(1L, "Новое название", "Новое описание"), result.getBody());

        verify(productService).save(payload);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new CreateProductDto("   ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, 
                () -> controller.save(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void createProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new CreateProductDto("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, 
                () -> controller.save(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void getProduct_ProductExists_ReturnsProduct() {
        // given
        var product = new Product(1L, "Название товара", "Описание товара");
        doReturn(product).when(productService).findById(1L);

        // when
        var result = controller.findById(1L);

        // then
        assertEquals(product, result);
    }

    @Test
    void findProduct_ReturnsProduct() {
        // given
        var product = new Product(1L, "Название товара", "Описание товара");
        doReturn(product).when(productService).findById(1L);

        // when
        var result = controller.findById(product.getId());

        // then
        assertEquals(product, result);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        // given
        var payload = new UpdateProductDto("Новое название", "Новое описание");
        var bindingResult = new MapBindingResult(Map.of(), "payload");

        // when
        var result = controller.patchUpdate(1L, payload, bindingResult);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(productService).update(1L, payload);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new UpdateProductDto("   ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> controller.patchUpdate(1L, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void updateProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new UpdateProductDto("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        // when
        var exception = assertThrows(BindException.class, () -> controller.patchUpdate(1L, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(productService);
    }

    @Test
    void deleteProduct_ReturnsNoContent() {
        // given

        // when
        var result = controller.delete(1L);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(productService).delete(1L);
    }

    @Test
    void handleNoSuchElementException_ReturnsNotFound() {
        // given
        var locale = Locale.forLanguageTag("ru");
        final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler(messageSource);

        // when
        final ResponseEntity<ProblemDetail> result = globalExceptionHandler.noElement(locale);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("Bad Request in e-commerce", result.getBody().getDetail());

        verifyNoInteractions(productService);
    }
}