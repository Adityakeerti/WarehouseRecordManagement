package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getAvailableProducts();
    Optional<Product> getProductById(Long id);
    Optional<Product> getProductByCode(String productCode);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String query);
} 