package com.example.ecapi.controller.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull Long productId, @Min(value = 1, message = "数量は1以上必要です") int quantity) {}
