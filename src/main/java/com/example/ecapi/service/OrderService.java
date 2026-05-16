package com.example.ecapi.service;

import com.example.ecapi.entity.Order;
import com.example.ecapi.entity.OrderItem;
import com.example.ecapi.entity.Product;
import com.example.ecapi.repository.OrderRepository;
import com.example.ecapi.repository.ProductRepository;
import com.example.ecapi.service.dto.OrderServiceDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注文サービス
 *
 * <p>「在庫チェック → 注文作成 → 在庫減算」を 1 トランザクションで実行することで、 途中で例外が発生した場合に全てロールバックされる。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  /** 全注文取得 */
  public List<OrderServiceDto.OrderResult> findAll() {
    return orderRepository.findAll().stream().map(OrderServiceDto.OrderResult::from).toList();
  }

  /** 注文詳細取得（JOIN FETCH で N+1 問題を回避） */
  public OrderServiceDto.OrderResult findById(Long id) {
    Order order =
        orderRepository
            .findByIdWithItems(id)
            .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: id=" + id));
    return OrderServiceDto.OrderResult.from(order);
  }

  /**
   * 注文作成
   *
   * <ol>
   *   <li>各商品の在庫チェック
   *   <li>注文エンティティ生成
   *   <li>在庫を減算
   *   <li>DB 保存
   * </ol>
   */
  @Transactional
  public OrderServiceDto.OrderResult create(OrderServiceDto.CreateCommand command) {
    List<OrderItem> items = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (OrderServiceDto.ItemCommand itemReq : command.items()) {
      Product product =
          productRepository
              .findById(itemReq.productId())
              .orElseThrow(
                  () -> new IllegalArgumentException("商品が見つかりません: id=" + itemReq.productId()));

      // 在庫チェック
      if (product.getStock() < itemReq.quantity()) {
        throw new IllegalStateException(
            String.format(
                "在庫不足: 商品[%s] 在庫=%d, 注文数=%d",
                product.getName(), product.getStock(), itemReq.quantity()));
      }

      // 在庫減算
      product.setStock(product.getStock() - itemReq.quantity());
      productRepository.save(product);

      OrderItem item =
          OrderItem.builder()
              .product(product)
              .quantity(itemReq.quantity())
              .unitPrice(product.getPrice())
              .build();
      items.add(item);
      totalAmount =
          totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity())));
    }

    Order order =
        Order.builder()
            .customerName(command.customerName())
            .status(Order.OrderStatus.PENDING)
            .totalAmount(totalAmount)
            .items(new ArrayList<>())
            .build();

    Order savedOrder = orderRepository.save(order);

    // 双方向リレーションの order 参照を設定してから再保存
    items.forEach(
        item -> {
          item.setOrder(savedOrder);
          savedOrder.getItems().add(item);
        });

    return OrderServiceDto.OrderResult.from(orderRepository.save(savedOrder));
  }

  /** 注文ステータス更新 */
  @Transactional
  public OrderServiceDto.OrderResult updateStatus(Long id, Order.OrderStatus newStatus) {
    Order order =
        orderRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: id=" + id));
    order.setStatus(newStatus);
    return OrderServiceDto.OrderResult.from(orderRepository.save(order));
  }
}
