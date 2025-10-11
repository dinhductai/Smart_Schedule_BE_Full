package com.microsv.order_service.service;

import com.microsv.order_service.dto.request.OrderRequest;
import com.microsv.order_service.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(Long orderId);
}
