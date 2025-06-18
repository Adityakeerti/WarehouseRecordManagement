package com.recursiveMind.WareHouseRecordManagement.repository.admin;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import jakarta.persistence.Entity;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("adminEntityManagerFactory")
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    Optional<Product> findBySku(String sku);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    List<Product> findAvailableProducts();
<<<<<<< HEAD
=======

    @Query("SELECT DISTINCT p FROM Product p JOIN OrderItem oi ON oi.product = p JOIN Order o ON oi.order = o WHERE o.status = 'DELIVERED'")
    List<Product> findProductsInDeliveredOrders();
>>>>>>> 5c4f977 (Done)
} 