package com.microsv.order_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class OrderResponse {
    private Long orderId;
    private UserResponse user; // Lồng thông tin user vào đây
    private ProductResponse product;

    public OrderResponse(Long orderId, UserResponse user, ProductResponse product) {
        this.orderId = orderId;
        this.user = user;
        this.product = product;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }
}