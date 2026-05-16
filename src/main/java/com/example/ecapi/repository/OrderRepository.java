package com.example.ecapi.repository;

import com.example.ecapi.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 注文リポジトリ */
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByCustomerName(String customerName);

  List<Order> findByStatus(Order.OrderStatus status);

  // LEFT JOIN FETCH で N+1 問題を回避して注文明細を一括取得
  @Query(
      "SELECT DISTINCT o FROM Order o "
          + "LEFT JOIN FETCH o.items i "
          + "LEFT JOIN FETCH i.product "
          + "WHERE o.id = :id")
  Optional<Order> findByIdWithItems(@Param("id") Long id);
}
