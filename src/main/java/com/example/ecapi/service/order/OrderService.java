package com.example.ecapi.service.order;

import com.example.ecapi.constant.OrderStatus;
import com.example.ecapi.entity.CustomerOrder;
import com.example.ecapi.entity.CustomerOrderDetail;
import com.example.ecapi.entity.Product;
import com.example.ecapi.repository.CutomerOrderRepository;
import com.example.ecapi.repository.ProductRepository;
import com.example.ecapi.service.order.dto.CreateOrder;
import com.example.ecapi.service.order.dto.CreateOrderItem;
import com.example.ecapi.service.order.dto.OrderResult;
import com.example.ecapi.service.order.mapper.OrderEntityMapper;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注文サービス
 *
 * <p>「在庫チェック → 注文作成 → 在庫減算」を 1 トランザクションで実行する。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final CutomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEntityMapper orderEntityMapper;

    /** 全注文取得 */
    public List<OrderResult> findAll() {
        return orderEntityMapper.toOrderResultList(orderRepository.findAll());
    }

    /** 注文詳細取得（JOIN FETCH で N+1 問題を回避） */
    public OrderResult findById(Long id) {
        CustomerOrder order =
                orderRepository
                        .findByIdWithItems(id)
                        .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: id=" + id));
        return orderEntityMapper.toOrderResult(order);
    }

    @Transactional
    public OrderResult create(CreateOrder createOrder) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(createOrder.customerName());
        order.setStatus(OrderStatus.PENDING);

        for (CreateOrderItem item : createOrder.items()) {
            Product product =
                    productRepository
                            .findById(item.productId())
                            .orElseThrow(
                                    () ->
                                            new EntityNotFoundException(
                                                    "商品が見つかりません: id=" + item.productId()));
            // 在庫チェック
            if (product.getStock() < item.quantity()) {
                throw new IllegalStateException(
                        String.format(
                                "在庫不足: 商品[%s] 在庫=%d, 注文数=%d",
                                product.getName(), product.getStock(), item.quantity()));
            }
            // 在庫減算
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);

            CustomerOrderDetail detail = new CustomerOrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.quantity());
            detail.setUnitPrice(product.getPrice()); // 価格取得
            detail.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
            order.getItems().add(detail);
        }

        order.setTotalAmount(
                order.getItems().stream()
                        .map(CustomerOrderDetail::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        CustomerOrder savedOrder = orderRepository.save(order);

        // MapStruct を使って Entity -> ServiceDto に変換
        return orderEntityMapper.toOrderResult(savedOrder);
    }

    @Transactional
    public OrderResult updateStatus(Long id, OrderStatus newStatus) {
        CustomerOrder order =
                orderRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません: id=" + id));
        order.setStatus(newStatus);
        CustomerOrder saved = orderRepository.save(order);
        return orderEntityMapper.toOrderResult(saved);
    }

    @Transactional
    public OrderResult cancel(Long id, String reason) {
        // ビジネスルールに応じて実装
        throw new UnsupportedOperationException("cancel 未実装です");
    }

    public List<OrderResult> search(String q, OrderStatus status) {
        // ここはリポジトリに検索クエリを追加する方が良い（ダミー実装）
        return findAll();
    }
}
