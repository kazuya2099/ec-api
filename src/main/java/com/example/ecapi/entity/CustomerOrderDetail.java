package com.example.ecapi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

/** 注文明細エンティティ */
@Entity
@Table(name = "customer_order_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerOrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private CustomerOrder order;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private int quantity;

  // 注文時の単価スナップショット（後から商品価格が変わっても注文額は変わらない）
  @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  public BigDecimal getSubtotal() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
