package com.example.commerceapp.controller;

import com.example.commerceapp.model.dto.CreateProductDto;
import com.example.commerceapp.model.dto.UpdateProductDto;
import com.example.commerceapp.model.entity.Product;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
class ProductControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getProduct_ProductExists_ReturnsProductPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1")
                .with(user("w.addams").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/api/v1/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                        }
                        """)));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("product/item"),
                        model().attribute("product", new Product(1L, "Товар", "Описание товара"))
                );
    }

    @Test
    void getProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1")
                .with(user("w.addams").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/api/v1/products/1")
                .willReturn(WireMock.notFound()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "No product with id 1")
                );
    }

    @Test
    void getProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1")
                .with(user("j.smith"));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void getProductEditPage_ProductExists_ReturnsProductEditPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1/edit")
                .with(user("w.addams").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/api/v1/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                        }
                        """)));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("product/edit"),
                        model().attribute("product", new Product(1L, "Товар", "Описание товара"))
                );
    }

    @Test
    void getProductEditPage_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1/edit")
                .with(user("w.addams").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/api/v1/products/1")
                .willReturn(WireMock.notFound()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "No product with id 1")
                );
    }

    @Test
    void getProductEditPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/1/edit")
                .with(user("j.smith"));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void updateProduct_RequestIsValid_RedirectsToProductPage() throws Exception {
        // given
        String title = "Новое название";
        String details = "Новое описание товара";
        String json = """
                        {
                            "title": "Новое название",
                            "details": "Новое описание товара"
                        }""";
        var requestBuilder = MockMvcRequestBuilders.post("/products/1")
                .param("title", title)
                .param("details", details)
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.patch("/api/v1/products/1")
                .withRequestBody(WireMock.equalToJson(json))
                .willReturn(WireMock.noContent()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/products")
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/api/v1/products/1"))
                .withRequestBody(WireMock.equalToJson(json)));
    }

    // TODO: 11.04.2024 explore IO bug
//    @Test
    void updateProduct_RequestIsInvalid_ReturnsProductEditPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/1")
                .param("title", "   ")
                .param("details", null)
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/api/v1/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                        }
                        """)));

        WireMock.stubFor(WireMock.patch("/api/v1/products/1")
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }"""))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("product/edit"),
                        model().attribute("product", new Product(1L, "Товар", "Описание товара")),
                        model().attribute("errors", List.of("Ошибка 1", "Ошибка 2")),
                        model().attribute("payload", new UpdateProductDto("   ", null))
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/api/v1/products/1"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }""")));
    }

    // TODO: 11.04.2024 explore IO bug
//    @Test
    void updateProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        WireMock.stubFor(WireMock.patch("/api/v1/products/1")
                .willReturn(WireMock.notFound()));

        var requestBuilder = MockMvcRequestBuilders.post("/products/1")
                .param("title", "Новое название")
                .param("details", "Новое описание товара")
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());


        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "No product with id 1 for update")
                );
    }

    @Test
    void updateProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/1/edit")
                .param("title", "Новое название")
                .param("details", "Новое описание товара")
                .with(user("j.smith"))
                .with(csrf());

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void deleteProduct_ProductExists_RedirectsToProductsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/1/delete")
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.delete("/api/v1/products/1")
                .willReturn(WireMock.noContent()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/products")
                );

        WireMock.verify(WireMock.deleteRequestedFor(WireMock.urlPathMatching("/api/v1/products/1")));
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/1/delete")
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/products/1")
                .willReturn(WireMock.notFound()));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("error/404"),
                        model().attribute("error", "No product with id 1 for delete")
                );
    }

    @Test
    void deleteProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/1/delete")
                .with(user("j.smith"))
                .with(csrf());

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void getProductList_ReturnsProductsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products")
                .queryParam("filter", "товар")
                .with(user("w.addams").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/api/v1/products"))
                .withQueryParam("filter", WireMock.equalTo("товар"))
                .willReturn(WireMock.ok("""
                        [
                            {"id": 1, "title": "Item 1", "details": "Item description 1"},
                            {"id": 2, "title": "Item 2", "details": "Item description 2"}
                        ]""").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("product/list"),
                        model().attribute("filter", "товар"),
                        model().attribute("productList", List.of(
                                new Product(1L, "Item 1", "Item description 1"),
                                new Product(2L, "Item 2", "Item description 2")
                        ))
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/api/v1/products"))
                .withQueryParam("filter", WireMock.equalTo("товар")));
    }

    @Test
    void getProductList_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products")
                .queryParam("filter", "товар")
                .with(user("j.smith"));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void getNewProductPage_ReturnsProductPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/add")
                .with(user("w.addams").roles("MANAGER"));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("product/add")
                );
    }

    @Test
    void getNewProductPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/products/add")
                .with(user("j.smith"));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    // TODO: 11.04.2024 explore IO bug
//    @Test
    void createProduct_RequestIsValid_RedirectsToProductPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products")
                .param("title", "Новый товар")
                .param("details", "Описание нового товара")
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "Новый товар",
                            "details": "Описание нового товара"
                        }"""))
                .willReturn(WireMock.created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                    "id": 1,
                                    "title": "Новый товар",
                                    "details": "Описание нового товара"
                                }""")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        header().string(HttpHeaders.LOCATION, "/api/v1/products/1")
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "Новый товар",
                            "details": "Описание нового товара"
                        }""")));
    }

    // TODO: 11.04.2024 explore IO bug
//    @Test
    void createProduct_RequestIsInvalid_ReturnsNewProductPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/add")
                .param("title", "   ")
                .with(user("w.addams").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }"""))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2"]
                                }""")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("product/add"),
                        model().attribute("payload", new CreateProductDto("   ", null)),
                        model().attribute("errors", List.of("Ошибка 1", "Ошибка 2"))
                );

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathMatching("/api/v1/products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "details": null
                        }""")));
    }

    @Test
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/products/add")
                .param("title", "Новый товар")
                .param("details", "Описание нового товара")
                .with(user("j.smith"))
                .with(csrf());

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}