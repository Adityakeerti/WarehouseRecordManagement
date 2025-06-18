package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.Product;
import com.recursiveMind.WareHouseRecordManagement.model.StockMovement;
import com.recursiveMind.WareHouseRecordManagement.repository.ProductRepository;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import com.recursiveMind.WareHouseRecordManagement.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.recursiveMind.WareHouseRecordManagement.repository.ActivityLogRepository;
import com.recursiveMind.WareHouseRecordManagement.model.ActivityLog;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public Product saveProduct(Product product) {
        boolean isNew = (product.getId() == null);
        Integer oldQuantity = null;
        if (!isNew) {
            oldQuantity = productRepository.findById(product.getId())
                .map(Product::getQuantity)
                .orElse(null);
        }
        Product saved = productRepository.save(product);

        // Defensive: Only log if product exists in DB and has a valid ID
        if (!isNew && saved.getId() != null && oldQuantity != null && !oldQuantity.equals(saved.getQuantity())) {
            if (productRepository.existsById(saved.getId())) {
                // Fetch managed product entity
                Product managedProduct = productRepository.findById(saved.getId()).orElseThrow();
                System.out.println("Logging stock movement for product ID: " + managedProduct.getId());
                StockMovement movement = StockMovement.builder()
                    .product(managedProduct)
                    .type(StockMovement.MovementType.ADJUST)
                    .quantity(saved.getQuantity() - oldQuantity)
                    .reference("Product Update")
                    .build();
                System.out.println("StockMovement: " + movement);
                stockMovementService.logMovement(movement);
            } else {
                System.out.println("Product with ID " + saved.getId() + " does not exist in DB. Skipping stock movement log.");
            }
        }

        // Log add or update action
        String action = isNew ? "ADD" : "UPDATE";
        String details = isNew ? "Product added" : "Product updated";
        ActivityLog log = ActivityLog.builder()
            .action(action)
            .productCode(saved.getProductCode())
            .productName(saved.getName())
            .details(details)
            .build();
        activityLogRepository.save(log);

        return saved;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    public List<Product> getOverstockedProducts() {
        return productRepository.findOverstockedProducts();
    }

    @Override
    public Long getTotalStockQuantity() {
        return productRepository.getTotalStockQuantity();
    }

    @Override
    public Long getLowStockCount() {
        return productRepository.countLowStockProducts();
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        productOpt.ifPresent(product -> {
            productRepository.deleteById(id);
            // Log deletion
            ActivityLog log = ActivityLog.builder()
                .action("DELETE")
                .productCode(product.getProductCode())
                .productName(product.getName())
                .details("Product deleted")
                .build();
            activityLogRepository.save(log);
        });
        // Optionally log deletion as a stock movement
    }

    @Override
    public Product updateStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setQuantity(quantity);
            Product saved = productRepository.save(product);
            StockMovement movement = StockMovement.builder()
                .product(saved)
                .type(StockMovement.MovementType.ADJUST)
                .quantity(quantity)
                .reference("Update Stock")
                .build();
            stockMovementService.logMovement(movement);
            return saved;
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public Product addStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setQuantity(product.getQuantity() + quantity);
            Product saved = productRepository.save(product);
            StockMovement movement = StockMovement.builder()
                .product(saved)
                .type(StockMovement.MovementType.IN)
                .quantity(quantity)
                .reference("Add Stock")
                .build();
            stockMovementService.logMovement(movement);
            return saved;
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public Product removeStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getQuantity() >= quantity) {
                product.setQuantity(product.getQuantity() - quantity);
                Product saved = productRepository.save(product);
                StockMovement movement = StockMovement.builder()
                    .product(saved)
                    .type(StockMovement.MovementType.OUT)
                    .quantity(quantity)
                    .reference("Remove Stock")
                    .build();
                stockMovementService.logMovement(movement);
                return saved;
            }
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }

    @Override
    public void logStockMovement(StockMovement movement) {
        stockMovementService.logMovement(movement);
    }

    @Override
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    public long getLowStockItemsCount() {
        return productRepository.countLowStockProducts();
    }

    @Override
    public double getTotalInventoryValue() {
        return productRepository.findAll().stream()
            .mapToDouble(p -> p.getPrice() * p.getQuantity())
            .sum();
    }

    @Override
    public Map<String, Long> getCategoryDistribution() {
        return productRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.counting()
            ));
    }

    @Override
    public Map<String, Integer> getStockLevelsByCategory() {
        return productRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.summingInt(Product::getQuantity)
            ));
    }

    @Override
    public List<Product> getLowStockItems() {
        return productRepository.findLowStockProducts();
    }
}
