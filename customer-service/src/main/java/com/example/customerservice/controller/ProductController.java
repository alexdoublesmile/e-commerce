package com.example.customerservice.controller;

import com.example.customerservice.client.ProductClient;
import com.example.customerservice.model.entity.FavouriteProduct;
import com.example.customerservice.model.entity.Product;
import com.example.customerservice.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;
    private final FavouriteService favouriteService;

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
                .map(FavouriteProduct::getProductId)
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
                .doOnNext(product -> model.addAttribute("product", product))
                .then(favouriteService.findByProductId(id)
                        .doOnNext(product -> model.addAttribute("isFavourite", true)))
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
}
