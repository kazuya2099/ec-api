package com.example.ecapi.service.product.mapper;

import com.example.ecapi.entity.Product;
import com.example.ecapi.service.product.dto.CreateProduct;
import com.example.ecapi.service.product.dto.ProductResult;
import com.example.ecapi.service.product.dto.UpdateProduct;
import java.util.List;
import org.mapstruct.*;

/** Entity <-> Service DTO のマッパー（MapStruct） */
@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    // Product entity -> ProductResult (service DTO)
    ProductResult toProductResult(Product product);

    List<ProductResult> toProductResultList(List<Product> products);

    // CreateProduct (service DTO) -> Product entity
    // Note: id/createdAt/updatedAt はマッピングしない（DB 側で生成される等）
    Product toProduct(CreateProduct dto);

    // 既存エンティティを UpdateProduct の内容で更新（null のフィールドは無視）
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromUpdate(UpdateProduct dto, @MappingTarget Product entity);
}
