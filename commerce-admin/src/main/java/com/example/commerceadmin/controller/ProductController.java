package com.example.commerceadmin.controller;

import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import com.example.commerceadmin.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String save(
            @Validated CreateProductDto productDto,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            final List<String> errorList = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("payload", productDto);
            model.addAttribute("errors", errorList);
            return "product/add";
        }
        Product savedProduct = productService.save(productDto);
        return "redirect:/products/" + savedProduct.getId();
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable("id") Long id,
            @Validated UpdateProductDto productDto,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            final List<String> errorList = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("product", productService.findById(id));
            model.addAttribute("payload", productDto);
            model.addAttribute("errors", errorList);
            return "product/edit";
        }
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
}
