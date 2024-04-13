package com.example.customerservice.model.entity;

import java.util.UUID;

public record Review(UUID id, Long productId, Integer rating, String text) {

}
