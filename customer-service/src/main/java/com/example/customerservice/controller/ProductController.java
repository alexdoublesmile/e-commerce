package com.example.customerservice.controller;

import com.example.customerservice.client.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClient productClient;

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

    @GetMapping("/{id:\\d+}")
    public Mono<String> getProductPage(
            @PathVariable("id") Long id,
            Model model) {

        return productClient.findById(id)
                .doOnNext(product -> model.addAttribute("product", product))
                .thenReturn("product/item");
    }
}
