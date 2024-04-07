package com.example.commerceadmin.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public String noElement(
            NoSuchElementException ex,
            Model model,
            HttpServletResponse response
    ) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }
}
