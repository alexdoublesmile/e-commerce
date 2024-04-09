package com.example.commerceservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

// TODO: 09.04.2024 add validation by rich model
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
    @NotBlank
    @Length(min = 2, max = 30)
    private String title;
    @Length(max = 1000)
    private String details;
}
