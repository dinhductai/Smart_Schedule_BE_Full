package com.example.product_service.service;

import com.example.product_service.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    Product getProduct(Long id);
    Product createProduct(Product product);
    void decreaseStock(Long id, Integer quantity);
}
