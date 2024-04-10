package com.example.commerceadmin.controller;

import com.example.commerceadmin.client.ProductClient;
import com.example.commerceadmin.exception.BadRequestException;
import com.example.commerceadmin.exception.GlobalExceptionHandler;
import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductClient productClient;

    @InjectMocks
    ProductController controller;

    @Test
    void getProductsList_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "товар";

        var products = LongStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i),
                        "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(productClient).findAll(filter);

        // when
        var result = controller.findAll(filter, model);

        // then
        assertNotNull(result);
        assertEquals("product/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("productList"));

        verify(productClient).findAll(filter);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = controller.getAddProductPage();

        // then
        assertEquals("product/add", result);
    }

    @Test
    @DisplayName("createProduct создаст новый товар и перенаправит на страницу товара")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var payload = new CreateProductDto("Новый товар", "Описание нового товара");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1L, "Новый товар", "Описание нового товара"))
                .when(productClient)
                .save(payload);

        // when
        var result = controller.save(payload, model, response);

        // then
        assertEquals("redirect:/products/1", result);

        verify(productClient).save(payload);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    @DisplayName("createProduct вернёт страницу с ошибками, если запрос невалиден")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors() {
        // given
        var payload = new CreateProductDto("  ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productClient)
                .save(payload);

        // when
        var result = controller.save(payload, model, response);

        // then
        assertNotNull(result);
        assertEquals("product/add", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productClient).save(payload);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void product_ProductExists_ReturnsProduct() {
        // given
        final Product product = new Product(1L, "Товар №1", "Описание товара №1");
        var model = new ConcurrentModel();
        doReturn(product).when(productClient).findById(1L);

        // when
        var result = controller.findById(1L, model);

        // then
        assertEquals("product/item", result);
        assertEquals(product, model.getAttribute("product"));

        verify(productClient).findById(1L);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void getProduct_ReturnsProductPage() {
        // given
        final Product product = new Product(1L, "Товар №1", "Описание товара №1");
        var model = new ConcurrentModel();
        doReturn(product).when(productClient).findById(1L);

        // when
        var result = controller.findById(1L,model);

        // then
        assertEquals("product/item", result);
        assertEquals(model.getAttribute("product"), product);

        verify(productClient).findById(1L);

        verifyNoMoreInteractions(productClient);
    }

    @Test
    void getProductEditPage_ReturnsProductEditPage() {
        // given
        final Product product = new Product(1L, "Товар №1", "Описание товара №1");
        var model = new ConcurrentModel();
        doReturn(product).when(productClient).findById(1L);

        // when
        var result = controller.getEditProductPage(1L, model);

        // then
        assertEquals("product/edit", result);
        assertEquals(model.getAttribute("product"), product);

        verify(productClient).findById(1L);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() {
        // given
        var product = new Product(1L, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductDto("Новое название", "Новое описание");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        // when
        var result = controller.update(1L, payload, model, response);

        // then
        assertEquals("redirect:/products", result);

        verify(productClient).update(1L, payload);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() {
        // given
        var product = new Product(1L, "Товар №1", "Описание товара №1");
        var payload = new UpdateProductDto("   ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productClient).update(1L, payload);

        // when
        var result = controller.update(1L, payload, model, response);

        // then
        assertEquals("product/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(productClient).update(1L, payload);
        verify(productClient).findById(1L);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void deleteProduct_RedirectsToProductsListPage() {
        // given
        var product = new Product(1L, "Товар №1", "Описание товара №1");

        // when
        var result = controller.delete(1L);

        // then
        assertEquals("redirect:/products", result);

        verify(productClient).delete(1L);
        verifyNoMoreInteractions(productClient);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        // given
        final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        String errorMessage = "not found";
        var exception = new NoSuchElementException(errorMessage);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        // when
        var result = globalExceptionHandler.noElement(exception, model, response);

        // then
        assertNotNull(result);
        assertEquals("error/404",  result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(model.getAttribute("error"), errorMessage);

        verifyNoInteractions(productClient);
    }
}