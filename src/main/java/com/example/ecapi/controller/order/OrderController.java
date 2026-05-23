package com.example.ecapi.controller.order;

import com.example.ecapi.constant.OrderStatus;
import com.example.ecapi.controller.order.dto.OrderRequest;
import com.example.ecapi.controller.order.dto.OrderResponse;
import com.example.ecapi.controller.order.mapper.OrderApiMapper;
import com.example.ecapi.service.order.OrderService;
import com.example.ecapi.service.order.dto.CreateOrder;
import com.example.ecapi.service.order.dto.OrderResult;
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
    private final OrderApiMapper orderApiMapper;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        List<OrderResult> result = orderService.findAll();
        List<OrderResponse> responses = orderApiMapper.toOrderResponseList(result);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        OrderResult result = orderService.findById(id);
        return ResponseEntity.ok(orderApiMapper.toOrderResponse(result));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        CreateOrder createOrder = orderApiMapper.toCreateOrder(request);
        OrderResult result = orderService.create(createOrder);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderApiMapper.toOrderResponse(result));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id, @RequestParam OrderStatus status) {
        OrderResult result = orderService.updateStatus(id, status);
        return ResponseEntity.ok(orderApiMapper.toOrderResponse(result));
    }
}
