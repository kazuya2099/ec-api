package com.example.ecapi.service.dto.product;

import com.example.ecapi.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

public sealed interface ProductServiceDto {

  @Builder
  record ProductCommand(String name, String description, BigDecimal price, int stock)
      implements ProductServiceDto {}

  @Builder
  record ProductResult(
      Long id,
      String name,
      String description,
      BigDecimal price,
      int stock,
      LocalDateTime createdAt,
      LocalDateTime updatedAt)
      implements ProductServiceDto {
    public static ProductResult from(Product p) {
      return ProductResult.builder()
          .id(p.getId())
          .name(p.getName())
          .description(p.getDescription())
          .price(p.getPrice())
          .stock(p.getStock())
          .createdAt(p.getCreatedAt())
          .updatedAt(p.getUpdatedAt())
          .build();
    }
  }
}
