package com.example.ecapi.service.product;

import com.example.ecapi.entity.Product;
import com.example.ecapi.repository.ProductRepository;
import com.example.ecapi.repository.ProductSpecification;
import com.example.ecapi.service.product.dto.CreateProduct;
import com.example.ecapi.service.product.dto.ProductResult;
import com.example.ecapi.service.product.dto.UpdateProduct;
import com.example.ecapi.service.product.mapper.ProductEntityMapper;
import java.math.BigDecimal; // BigDecimal をインポート
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final ProductEntityMapper productEntityMapper;

    /** 全商品取得 */
    public List<ProductResult> findAll() {
        List<Product> products = productRepository.findAll();
        return productEntityMapper.toProductResultList(products);
    }

    /** ID 指定で商品取得 */
    public ProductResult findById(Long id) {
        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: id=" + id));
        return productEntityMapper.toProductResult(product);
    }

    /** 商品名キーワード検索 */
    // このメソッドは、より汎用的な searchProducts に置き換えられる可能性がありますが、既存のコードとの整合性を保つため残します。
    public List<ProductResult> search(String keyword) {
        return productEntityMapper.toProductResultList(
                productRepository.findByNameContainingIgnoreCase(keyword));
    }

    /**
     * 商品検索
     * name, description, price のいずれか、またはすべてで AND 検索を行います。
     * いずれのパラメータも null の場合は全件検索を行います。
     */
    public List<ProductResult> searchProducts(String name, String description, BigDecimal price) {
        Specification<Product> spec = ProductSpecification.byCriteria(name, description, price);
        List<Product> products = productRepository.findAll(spec, Sort.by("name").ascending());
        return productEntityMapper.toProductResultList(products);
    }

    /** 商品登録 */
    @Transactional
    public ProductResult create(CreateProduct createProduct) {
        Product product = productEntityMapper.toProduct(createProduct);
        return productEntityMapper.toProductResult(productRepository.save(product));
    }

    /** 商品更新 */
    @Transactional
    public ProductResult update(Long id, UpdateProduct updateProduct) {
        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません: id=" + id));
        productEntityMapper.updateProductFromUpdate(updateProduct, product);
        return productEntityMapper.toProductResult(productRepository.save(product));
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
