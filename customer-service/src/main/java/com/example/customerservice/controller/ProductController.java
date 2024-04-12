package com.example.customerservice.controller;

import com.example.customerservice.client.ProductClient;
import com.example.customerservice.model.dto.CreateReviewDto;
import com.example.customerservice.model.entity.Favourite;
import com.example.customerservice.service.FavouriteService;
import com.example.customerservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;
    private final FavouriteService favouriteService;
    private final ReviewService reviewService;

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

        return favouriteService.findAll()
                .map(Favourite::getProductId)
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
                .then(favouriteService.findByProductId(id)
                        .doOnNext(product -> model.addAttribute("isFavourite", true)))
                .then(reviewService.findAllByProductId(id)
                        .collectList()
                        .doOnNext(reviewList -> model.addAttribute("reviewList", reviewList)))
                .thenReturn("product/item");
    }

    @PostMapping("/{id:\\d+}/add-to-favourites")
    public Mono<String> addToFavourite(@PathVariable("id") Long id) {
        return favouriteService.add(id)
                .thenReturn("redirect:/customer/products/%d".formatted(id));
    }

    @PostMapping("/{id:\\d+}/remove-from-favourites")
    public Mono<String> removeFromFavourite(@PathVariable("id") Long id) {
        return favouriteService.remove(id)
                .thenReturn("redirect:/customer/products/%d".formatted(id));
    }

    @PostMapping("/{id:\\d+}/add-review")
    public Mono<String> addReview(
            @PathVariable("id") Long productId,
            @Validated CreateReviewDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("payload", dto);
            model.addAttribute("errors", bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());

            return getProductPage(productId, model);
        } else {
            return reviewService.add(productId, dto)
                    .thenReturn("redirect:/customer/products/%d".formatted(productId));
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "error/404";
    }
}
