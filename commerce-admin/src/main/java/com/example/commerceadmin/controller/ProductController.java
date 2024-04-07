package com.example.commerceadmin.controller;

import com.example.commerceadmin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
}
