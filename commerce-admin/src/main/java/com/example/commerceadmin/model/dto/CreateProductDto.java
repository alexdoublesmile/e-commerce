package com.example.commerceadmin.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
    private String title;
    private String details;
}
