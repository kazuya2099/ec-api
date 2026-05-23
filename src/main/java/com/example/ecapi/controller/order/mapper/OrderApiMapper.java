package com.example.ecapi.controller.order.mapper;

import com.example.ecapi.controller.order.dto.OrderRequest;
import com.example.ecapi.controller.order.dto.OrderResponse;
import com.example.ecapi.service.order.dto.CreateOrder;
import com.example.ecapi.service.order.dto.OrderResult;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderApiMapper {

    CreateOrder toCreateOrder(OrderRequest request);

    OrderResponse toOrderResponse(OrderResult result);

    List<OrderResponse> toOrderResponseList(List<OrderResult> results);
}
