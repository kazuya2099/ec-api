package com.example.ecapi.controller.product;

import com.example.ecapi.controller.product.dto.CreateProductRequest;
import com.example.ecapi.controller.product.dto.ProductResponse;
import com.example.ecapi.controller.product.dto.UpdateProductRequest;
import com.example.ecapi.controller.product.mapper.ProductApiMapper;
import com.example.ecapi.service.product.ProductService;
import com.example.ecapi.service.product.dto.ProductResult;
import jakarta.validation.Valid;
import java.math.BigDecimal; // BigDecimal をインポート
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
    private final ProductApiMapper productApiMapper;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price
    ) {
        name = name == null ? null : name.trim();
        description = description == null ? null : description.trim();
        List<ProductResult> results = productService.searchProducts(name, description, price);
        return ResponseEntity.ok(productApiMapper.toProductResponseList(results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productApiMapper.toProductResponse(productService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResult result = productService.create(productApiMapper.toCreateProduct(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productApiMapper.toProductResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        ProductResult result = productService.update(id, productApiMapper.toUpdateProduct(request));
        return ResponseEntity.ok(productApiMapper.toProductResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
