package com.microsv.order_service.service.impl;

import com.microsv.order_service.dto.response.ProductResponse;
import com.microsv.order_service.feign.ProductClient;
import com.microsv.order_service.feign.UserClient;
import com.microsv.order_service.dto.request.OrderRequest;
import com.microsv.order_service.dto.response.OrderResponse;
import com.microsv.order_service.dto.response.UserResponse;
import com.microsv.order_service.entity.Order;
import com.microsv.order_service.repository.OrderRepository;
import com.microsv.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        productClient.decreaseStock(orderRequest.getProductId(), orderRequest.getQuantity());
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setProductId(orderRequest.getProductId());
        Order savedOrder = orderRepository.save(order);

        UserResponse userResponse = userClient.getUserById(savedOrder.getUserId());
        ProductResponse productResponse = productClient.getProductById(savedOrder.getProductId());

        return new OrderResponse(savedOrder.getId(), userResponse, productResponse);
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
    // 1. Lấy thông tin order từ CSDL của chính nó
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. Gọi sang user-service để lấy thông tin user
        UserResponse userResponse = userClient.getUserById(order.getUserId());
        ProductResponse productResponse = productClient.getProductById(order.getProductId());
        // 3. Kết hợp thông tin và trả về
        return new OrderResponse(order.getId(), userResponse,productResponse);
    }
}
