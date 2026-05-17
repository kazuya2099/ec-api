package com.example.ecapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

/**
 * 商品エンティティ
 *
 * <p>Hibernate 7 / Jakarta EE 11 ベース。 @Column の nullable は DB スキーマ生成時の制約。 実行時バリデーションは Bean
 * Validation (@NotBlank 等) で行う。
 */
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "商品名は必須です")
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String name;

  @Size(max = 500)
  @Column(length = 500)
  private String description;

  @NotNull(message = "価格は必須です")
  @DecimalMin(value = "0.0", inclusive = false, message = "価格は0より大きい必要があります")
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Min(value = 0, message = "在庫数は0以上である必要があります")
  @Column(nullable = false)
  private int stock;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
