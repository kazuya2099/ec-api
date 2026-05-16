package com.example.ecapi.service;

import com.example.ecapi.entity.Product;
import com.example.ecapi.repository.ProductRepository;
import com.example.ecapi.service.dto.ProductServiceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品サービス
 *
 * <p>ビジネスロジックはここに集約する。 Controller はサービスの呼び出しと HTTP レスポンスへの変換のみ担当。
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;

  /** 全商品取得 */
  public List<ProductServiceDto.ProductResult> findAll() {
    return productRepository.findAll().stream().map(ProductServiceDto.ProductResult::from).toList();
  }

  /** ID 指定で商品取得 */
  public ProductServiceDto.ProductResult findById(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: id=" + id));
    return ProductServiceDto.ProductResult.from(product);
  }

  /** 商品名キーワード検索 */
  public List<ProductServiceDto.ProductResult> search(String keyword) {
    return productRepository.findByNameContainingIgnoreCase(keyword).stream()
        .map(ProductServiceDto.ProductResult::from)
        .toList();
  }

  /** 商品登録 */
  @Transactional
  public ProductServiceDto.ProductResult create(ProductServiceDto.ProductCommand command) {
    Product product =
        Product.builder()
            .name(command.name())
            .description(command.description())
            .price(command.price())
            .stock(command.stock())
            .build();
    return ProductServiceDto.ProductResult.from(productRepository.save(product));
  }

  /** 商品更新 */
  @Transactional
  public ProductServiceDto.ProductResult update(Long id, ProductServiceDto.ProductCommand command) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: id=" + id));
    product.setName(command.name());
    product.setDescription(command.description());
    product.setPrice(command.price());
    product.setStock(command.stock());
    return ProductServiceDto.ProductResult.from(productRepository.save(product));
  }

  /** 商品削除 */
  @Transactional
  public void delete(Long id) {
    if (!productRepository.existsById(id)) {
      throw new IllegalArgumentException("商品が見つかりません: id=" + id);
    }
    productRepository.deleteById(id);
  }
}
