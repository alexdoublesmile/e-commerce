package com.example.feedbackservice.model.dto;

import jakarta.validation.constraints.NotNull;

public record CreateFavouriteDto(@NotNull Long productId) {
}
