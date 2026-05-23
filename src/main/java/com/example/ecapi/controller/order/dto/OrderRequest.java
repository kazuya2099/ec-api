package com.example.ecapi.controller.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OrderRequest(
        @NotBlank(message = "顧客名は必須です") String customerName,
        @NotEmpty(message = "注文商品は1つ以上必要です") @Valid List<OrderItemRequest> items) {}
