package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.StockMovement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Optional<Product> getProductByCode(String productCode);
    List<Product> getProductsByCategory(String category);
    List<Product> searchProducts(String name);
    List<Product> getLowStockProducts();
    List<Product> getOverstockedProducts();
    Long getTotalStockQuantity();
    Long getLowStockCount();
    void deleteProduct(Long id);
    Product updateStock(Long productId, int quantity);
    Product addStock(Long productId, int quantity);
    Product removeStock(Long productId, int quantity);
    void logStockMovement(StockMovement movement);
    
    // New methods for dashboard
    long getTotalProducts();
    long getLowStockItemsCount();
    double getTotalInventoryValue();
    Map<String, Long> getCategoryDistribution();
    Map<String, Integer> getStockLevelsByCategory();
    List<Product> getLowStockItems();
}
