package com.example.customerservice.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClientBadRequestException extends Throwable {

    private final List<String> errors = new ArrayList<>();

    public ClientBadRequestException(Throwable ex, List<String> errors) {
        super(ex);
        this.errors.addAll(errors);
    }
}
