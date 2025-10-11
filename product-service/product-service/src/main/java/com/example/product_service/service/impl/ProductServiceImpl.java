package com.example.product_service.service.impl;

import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));
    }

    @Override
    public Product createProduct(Product product) {
        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setPrice(product.getPrice());
        newProduct.setDescription(product.getDescription());
        newProduct.setStockQuantity(product.getStockQuantity());
        return productRepository.save(newProduct);
    }

    @Override
    public void decreaseStock(Long id, Integer quantity) {
        Product product = getProduct(id);
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product ID: " + id);
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }
}
