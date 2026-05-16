package com.example.ecapi.service.dto;

import com.example.ecapi.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public sealed interface OrderServiceDto {

  /** 注文作成用の入力データ */
  @Builder
  record CreateCommand(String customerName, List<ItemCommand> items) implements OrderServiceDto {}

  record ItemCommand(Long productId, int quantity) implements OrderServiceDto {}

  /** サービスから返す処理結果データ */
  @Builder
  record OrderResult(
      Long id,
      String customerName,
      String status,
      BigDecimal totalAmount,
      List<ItemResult> items,
      LocalDateTime orderedAt,
      LocalDateTime updatedAt)
      implements OrderServiceDto {
    public static OrderResult from(Order order) {
      return OrderResult.builder()
          .id(order.getId())
          .customerName(order.getCustomerName())
          .status(order.getStatus().name())
          .totalAmount(order.getTotalAmount())
          .items(
              order.getItems().stream()
                  .map(
                      item ->
                          new ItemResult(
                              item.getProduct().getId(),
                              item.getProduct().getName(),
                              item.getQuantity(),
                              item.getUnitPrice(),
                              item.getSubtotal()))
                  .toList())
          .orderedAt(order.getCreatedAt())

          .build();
    }
  }

  record ItemResult(
      Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal)
      implements OrderServiceDto {}
}
