package com.example.ecapi.controller.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "商品名は必須です") String name,
        String description,
        @NotNull(message = "価格は必須です") @DecimalMin(value = "0.0", inclusive = false)
                BigDecimal price,
        @Min(value = 0, message = "在庫数は0以上で指定してください") int stock) {}
