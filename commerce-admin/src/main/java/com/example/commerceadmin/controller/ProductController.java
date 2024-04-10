package com.example.commerceadmin.controller;

import com.example.commerceadmin.client.ProductClient;
import com.example.commerceadmin.exception.BadRequestException;
import com.example.commerceadmin.model.dto.CreateProductDto;
import com.example.commerceadmin.model.dto.UpdateProductDto;
import com.example.commerceadmin.model.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// TODO: 09.04.2024 try to use JS for view
// TODO: 09.04.2024 try to use Vaadin, Swing, JavaFX for view
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductClient productClient;

    @GetMapping
    public String findAll(
            @RequestParam(name = "filter", required = false) String filter,
            Model model) {
        model.addAttribute("productList", productClient.findAll(filter));
        model.addAttribute("filter", filter);
        return "product/list";
    }

    @GetMapping("/{id:\\d+}")
    public String findById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productClient.findById(id));
        return "product/item";
    }

    @PostMapping
    public String save(
            @Validated CreateProductDto productDto,
            Model model,
            HttpServletResponse response) {
        try {
            Product savedProduct = productClient.save(productDto);
            return "redirect:/products/" + savedProduct.getId();
        } catch (BadRequestException ex) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("payload", productDto);
            model.addAttribute("errors", ex.getErrors());
            return "product/add";
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable("id") Long id,
            @Validated UpdateProductDto productDto,
            Model model,
            HttpServletResponse response) {
        try {
            productClient.update(id, productDto);
            return "redirect:/products";
        } catch (BadRequestException ex) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("product", productClient.findById(id));
            model.addAttribute("payload", productDto);
            model.addAttribute("errors", ex.getErrors());
            return "product/edit";
        }
    }

    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable("id") Long id) {
        productClient.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/add")
    public String getAddProductPage() {
        return "product/add";
    }

    @GetMapping("/{id:\\d+}/edit")
    public String getEditProductPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productClient.findById(id));
        return "product/edit";
    }
}
