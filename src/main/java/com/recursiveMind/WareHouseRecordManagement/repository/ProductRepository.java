package com.recursiveMind.WareHouseRecordManagement.repository;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    Optional<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.quantity >= p.maxStockLevel")
    List<Product> findOverstockedProducts();
    
    @Query("SELECT SUM(p.quantity) FROM Product p")
    Long getTotalStockQuantity();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.minStockLevel")
    Long countLowStockProducts();
} 