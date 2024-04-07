package com.example.commerceadmin.controller;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.entity.Product;
import com.example.commerceadmin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("productList", productService.findAll());
        return "product/list";
    }

    @GetMapping("/add")
    public String getAddProductPage() {
        return "product/add";
    }

    @PostMapping
    public String save(CreateProductDto productDto) {
        Product savedProduct = productService.save(productDto);
        return "redirect:/products";
    }

    @GetMapping("/{id:\\d+}")
    public String findById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/item";
    }
}
