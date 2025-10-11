package com.microsv.order_service.feign;
import com.microsv.order_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service",path = "/api/products")
public interface ProductClient {
    // Dùng để lấy thông tin sản phẩm (giá, tên,...)
    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);

    // Dùng để giảm số lượng tồn kho
    @PostMapping("/{id}/decrease-stock")
    void decreaseStock(@PathVariable("id") Long id, @RequestParam Integer quantity);

}
