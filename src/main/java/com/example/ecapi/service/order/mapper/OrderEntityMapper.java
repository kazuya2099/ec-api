package com.example.ecapi.service.order.mapper;

import com.example.ecapi.constant.OrderStatus;
import com.example.ecapi.entity.CustomerOrder;
import com.example.ecapi.entity.CustomerOrderDetail;
import com.example.ecapi.service.order.dto.OrderResult;
import com.example.ecapi.service.order.dto.OrderResultItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

    @Mapping(source = "createdAt", target = "orderedAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "status", target = "status")
    OrderResult toOrderResult(CustomerOrder order);

    List<OrderResult> toOrderResultList(List<CustomerOrder> orders);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(source = "subtotal", target = "subtotal")
    OrderResultItem toOrderResultItem(CustomerOrderDetail detail);

    List<OrderResultItem> toOrderResultItemList(List<CustomerOrderDetail> details);

    default String mapStatus(OrderStatus status) {
        return status == null ? null : status.name();
    }
}
