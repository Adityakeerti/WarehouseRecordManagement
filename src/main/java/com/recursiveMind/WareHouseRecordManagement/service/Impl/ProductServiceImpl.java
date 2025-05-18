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
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementService stockMovementService;

    @Override
    public Product saveProduct(Product product) {
        boolean isNew = (product.getId() == null);
        Product saved = productRepository.save(product);
        if (!isNew) { // Only log for updates, not for new products
            StockMovement movement = StockMovement.builder()
                .product(saved)
                .type(StockMovement.MovementType.IN)
                .quantity(saved.getQuantity())
                .reference("Product Save")
                .build();
            stockMovementService.logMovement(movement);
        }
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
        productRepository.deleteById(id);
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
}
