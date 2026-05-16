package com.example.ecapi.controller;

import com.example.ecapi.controller.dto.ProductDto.*;
import com.example.ecapi.service.ProductService;
import com.example.ecapi.service.dto.ProductServiceDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 商品 REST コントローラー
 *
 * <pre>
 * GET    /api/products          全商品取得
 * GET    /api/products?q=xxx    商品名検索
 * GET    /api/products/{id}     商品詳細
 * POST   /api/products          商品登録
 * PUT    /api/products/{id}     商品更新
 * DELETE /api/products/{id}     商品削除
 * </pre>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAll(@RequestParam(required = false) String q) {
    List<ProductServiceDto.ProductResult> results;
    if (q != null && !q.isBlank()) {
      results = productService.search(q);
    } else {
      results = productService.findAll();
    }
    return ResponseEntity.ok(results.stream().map(this::toResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(toResponse(productService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    ProductServiceDto.ProductResult result = productService.create(toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> update(
      @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    ProductServiceDto.ProductResult result = productService.update(id, toCommand(request));
    return ResponseEntity.ok(toResponse(result));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // --- 変換用プライベートメソッド ---

  private ProductResponse toResponse(ProductServiceDto.ProductResult result) {
    return ProductResponse.builder()
        .id(result.id())
        .name(result.name())
        .description(result.description())
        .price(result.price())
        .stock(result.stock())
        .createdAt(result.createdAt())
        .updatedAt(result.updatedAt())
        .build();
  }

  private ProductServiceDto.ProductCommand toCommand(ProductRequest request) {
    return ProductServiceDto.ProductCommand.builder()
        .name(request.name())
        .description(request.description())
        .price(request.price())
        .stock(request.stock())
        .build();
  }
}
