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
    
    @Column(unique = true)
    private String sku;
    
    private String location;
    
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
    private final DoubleProperty priceProperty = new SimpleDoubleProperty();
    
    @PostLoad
    public void initializeProperties() {
        codeProperty.set(productCode);
        nameProperty.set(name);
        quantityProperty.set(quantity);
        priceProperty.set(price);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { 
        this.productCode = productCode;
        this.codeProperty.set(productCode);
    }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        this.nameProperty.set(name);
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        this.quantityProperty.set(quantity);
    }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { 
        this.price = price;
        this.priceProperty.set(price);
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isActive() { return this.quantity > 0; }
    
    // JavaFX Property getters
    public StringProperty codeProperty() { return codeProperty; }
    public StringProperty nameProperty() { return nameProperty; }
    public IntegerProperty quantityProperty() { return quantityProperty; }
    public DoubleProperty priceProperty() { return priceProperty; }
    
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