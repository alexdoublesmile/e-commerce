package com.example.customerservice.controller;

import com.example.customerservice.client.FavouriteClient;
import com.example.customerservice.client.ProductClient;
import com.example.customerservice.client.ReviewClient;
import com.example.customerservice.exception.ClientBadRequestException;
import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Favourite;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Log4j2
@Controller
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;
    private final ReviewClient reviewClient;
    private final FavouriteClient favouriteClient;

    @GetMapping("/list")
    public Mono<String> getProductListPage(
            @RequestParam(required = false, name = "filter") String filter,
            Model model) {

        model.addAttribute("filter", filter);

        return productClient.findAll(filter)
                .collectList()
                .doOnNext(productList -> model.addAttribute("productList", productList))
                .thenReturn("product/list");
    }

    @GetMapping("/favourites")
    public Mono<String> getFavouriteListPage(
            @RequestParam(required = false, name = "filter") String filter,
            Model model) {

        model.addAttribute("filter", filter);

        return favouriteClient.findAllFavourite()
                .map(Favourite::productId)
                .collectList()
                .flatMap(favourites -> productClient.findAll(filter)
                        .filter(product -> favourites.contains(product.id()))
                        .collectList()
                .doOnNext(productList -> model.addAttribute("productList", productList)))
                .thenReturn("product/favourites");
    }

    @GetMapping("/{id:\\d+}")
    public Mono<String> getProductPage(
            @PathVariable("id") Long id,
            Model model) {

        model.addAttribute("isFavourite", false);

        return productClient.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")))
                .doOnNext(product -> model.addAttribute("product", product))
                .then(favouriteClient.findFavouriteByProductId(id)
                        .doOnNext(product -> model.addAttribute("isFavourite", true)))
                .then(reviewClient.findAllReviewByProductId(id)
                        .collectList()
                        .doOnNext(reviewList -> model.addAttribute("reviewList", reviewList)))
                .thenReturn("product/item");
    }

    @PostMapping("/{id:\\d+}/add-to-favourites")
    public Mono<String> addToFavourite(@PathVariable("id") Long id) {
        return favouriteClient.addFavourite(id)
                .thenReturn("redirect:/customer/products/%d".formatted(id))
                .onErrorResume(ex -> {
                    log.error(ex.getMessage(), ex);
                    return Mono.just("redirect:/customer/products/%d".formatted(id));
                });
    }

    @PostMapping("/{id:\\d+}/remove-from-favourites")
    public Mono<String> removeFromFavourite(@PathVariable("id") Long id) {
        return favouriteClient.removeFavourite(id)
                .thenReturn("redirect:/customer/products/%d".formatted(id));
    }

    @PostMapping("/{id:\\d+}/add-review")
    public Mono<String> addReview(
            @PathVariable("id") Long productId,
            CreateReviewDto dto,
            Model model) {
        return reviewClient.addReview(productId, dto)
                .thenReturn("redirect:/customer/products/%d".formatted(productId))
                .onErrorResume(ClientBadRequestException.class, ex -> {
                    model.addAttribute("payload", dto);
                    model.addAttribute("errors", ex.getErrors());

                    return getProductPage(productId, model);
                });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model, ServerHttpResponse response) {
        model.addAttribute("error", exception.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND);
        return "error/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrf(ServerWebExchange exchange) {
        final Mono<CsrfToken> tokenMono = exchange.getAttribute(CsrfToken.class.getName());
        return tokenMono.doOnSuccess(token ->
                exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
