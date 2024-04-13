package com.example.feedbackservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private UUID id;

    private Long productId;

    private Integer rating;

    private String text;
}
