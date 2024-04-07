package com.example.commerceadmin.controller;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import com.example.commerceadmin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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

    @GetMapping("/{id:\\d+}")
    public String findById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/item";
    }

    @PostMapping
    public String save(CreateProductDto productDto) {
        Product savedProduct = productService.save(productDto);
        return "redirect:/products/" + savedProduct.getId();
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id, UpdateProductDto productDto) {
        productService.update(id, productDto);
        return "redirect:/products";
    }

    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/add")
    public String getAddProductPage() {
        return "product/add";
    }

    @GetMapping("/{id:\\d+}/edit")
    public String getEditProductPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/edit";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String noElement(NoSuchElementException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }
}
