package com.recursiveMind.WareHouseRecordManagement.model;

import jakarta.persistence.*;
import lombok.*;
import javafx.beans.property.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_code", unique = true, nullable = false)
    private String productCode;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "min_stock_level")
    private Integer minStockLevel;
    
    @Column(name = "max_stock_level")
    private Integer maxStockLevel;
    
    @Column(unique = true)
    private String sku;
    
    private String location;
    
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // JavaFX Properties
    @Transient
    private final StringProperty codeProperty = new SimpleStringProperty();
    @Transient
    private final StringProperty nameProperty = new SimpleStringProperty();
    @Transient
    private final IntegerProperty quantityProperty = new SimpleIntegerProperty();
    @Transient
    private final IntegerProperty minStockProperty = new SimpleIntegerProperty();
    
    @PostLoad
    public void initializeProperties() {
        codeProperty.set(productCode);
        nameProperty.set(name);
        quantityProperty.set(quantity);
        minStockProperty.set(minStockLevel);
    }
    
    public StringProperty codeProperty() {
        return codeProperty;
    }
    
    public StringProperty nameProperty() {
        return nameProperty;
    }
    
    public IntegerProperty quantityProperty() {
        return quantityProperty;
    }
    
    public IntegerProperty minStockProperty() {
        return minStockProperty;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
