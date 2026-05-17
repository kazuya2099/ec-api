package com.example.ecapi.controller.order;

import com.example.ecapi.constant.OrderStatus;
import com.example.ecapi.controller.dto.order.OrderDto;
import com.example.ecapi.service.OrderService;
import com.example.ecapi.service.dto.order.OrderServiceDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 注文 REST コントローラー
 *
 * <pre>
 * GET   /api/orders                      全注文取得
 * GET   /api/orders/{id}                 注文詳細
 * POST  /api/orders                      注文作成（在庫チェックあり）
 * PATCH /api/orders/{id}/status          ステータス更新
 * </pre>
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping
  public ResponseEntity<List<OrderDto.OrderResponse>> getAll() {
    List<OrderDto.OrderResponse> responses =
        orderService.findAll().stream().map(this::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDto.OrderResponse> getById(@PathVariable Long id) {
    OrderServiceDto.OrderResult result = orderService.findById(id);
    return ResponseEntity.ok(toResponse(result));
  }

  @PostMapping
  public ResponseEntity<OrderDto.OrderResponse> create(
      @Valid @RequestBody OrderDto.OrderRequest request) {
    OrderServiceDto.CreateCommand command = toCommand(request);
    OrderServiceDto.OrderResult result = orderService.create(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<OrderDto.OrderResponse> updateStatus(
      @PathVariable Long id, @RequestParam OrderStatus status) {
    OrderServiceDto.OrderResult result = orderService.updateStatus(id, status);
    return ResponseEntity.ok(toResponse(result));
  }

  // --- 変換用プライベートメソッド ---

  private OrderDto.OrderResponse toResponse(OrderServiceDto.OrderResult result) {
    return OrderDto.OrderResponse.builder()
        .id(result.id())
        .customerName(result.customerName())
        .status(result.status())
        .totalAmount(result.totalAmount())
        .items(
            result.items().stream()
                .map(
                    i ->
                        new OrderDto.OrderItemResponse(
                            i.productId(),
                            i.productName(),
                            i.quantity(),
                            i.unitPrice(),
                            i.subtotal()))
                .toList())
        .orderedAt(result.orderedAt())
        .build();
  }

  private OrderServiceDto.CreateCommand toCommand(OrderDto.OrderRequest request) {
    return OrderServiceDto.CreateCommand.builder()
        .customerName(request.customerName())
        .items(
            request.items().stream()
                .map(i -> new OrderServiceDto.ItemCommand(i.productId(), i.quantity()))
                .toList())
        .build();
  }
}
