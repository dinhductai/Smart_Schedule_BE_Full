package com.microsv.order_service.controller;

import com.microsv.order_service.dto.request.OrderRequest;
import com.microsv.order_service.dto.response.OrderResponse;
import com.microsv.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import feign.FeignException; // <-- THÊM IMPORT NÀY

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse createdOrder = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (FeignException.BadRequest e) {
            // Bắt lỗi 400 từ Feign và trả về chính xác thông báo lỗi của product-service
            return ResponseEntity.badRequest().body(e.contentUTF8());
        } catch (RuntimeException e) {
            // Bắt các lỗi chung khác (ví dụ: không tìm thấy user)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        try {
            OrderResponse orderResponse = orderService.getOrderById(id);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            // Xử lý khi không tìm thấy order hoặc user
            return ResponseEntity.notFound().build();
        }
    }
}