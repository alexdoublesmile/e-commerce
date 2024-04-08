package com.example.commerceadmin.client;

import com.example.commerceadmin.exception.BadRequestException;
import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

@Log4j2
@RequiredArgsConstructor
public class RestProductClient implements ProductClient {
    private static final ParameterizedTypeReference<List<Product>> PRODUCT_LIST_TYPE_REFERENCE
            = new ParameterizedTypeReference<>(){};
    // TODO: 08.04.2024 try to use HTTP interfaces
    private final RestClient restClient;

    @Override
    public List<Product> findAll() {
        return restClient
                .get()
                .uri("/products")
                .retrieve()
                .body(PRODUCT_LIST_TYPE_REFERENCE);
    }

    @Override
    public Product findById(Long id) {
        try {
            return restClient
                    .get()
                    .uri("/products/{id}",id)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NoSuchElementException(format(
                    "No product with id %s", id));
        }
    }

    @Override
    public Product save(CreateProductDto productDto) {
        try {
            return restClient
                    .post()
                    .uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productDto)
                    .retrieve()
                    .body(Product.class);
        } catch (HttpClientErrorException.BadRequest ex) {
            // TODO: 08.04.2024 make normal either or smth
            final ProblemDetail problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void update(Long id, UpdateProductDto productDto) {
        try {
            restClient
                    .patch()
                    .uri("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NoSuchElementException(format(
                    "No product with id %s for update", id));
        } catch (HttpClientErrorException.BadRequest ex) {
            // TODO: 08.04.2024 make normal either or smth
            final ProblemDetail problemDetail = ex.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void delete(Long id) {
        try {
            restClient
                    .delete()
                    .uri("/products/{id}",id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NoSuchElementException(format(
                    "No product with id %s for delete", id));
        }
    }
}
