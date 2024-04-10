package com.example.commerceservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    // TODO: 08.04.2024 add i18n
    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> noElement(BindException ex, Locale locale) {
//        final String errormessage = Objects.requireNonNull(messageSource.getMessage(
//                "errors.400.title", new Object[0], "errors.400.title", locale));

        final ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Bad Request in e-commerce");
        problemDetail.setProperty("errors", ex.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList());

        return ResponseEntity
                .badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> noElement(Locale locale) {
//        final String errormessage = Objects.requireNonNull(messageSource.getMessage(
//                "errors.404.title", new Object[0], "errors.404.title", locale));

        final ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, "Bad Request in e-commerce");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
}
