package com.recursiveMind.WareHouseRecordManagement.model;

import jakarta.persistence.*;
import lombok.*;
import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Column(length = 1000)
    private String notes;
    
    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;
    
    @Column(name = "billing_address", length = 500)
    private String billingAddress;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_status")
    private String paymentStatus;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // JavaFX Properties
    @Transient
    private final StringProperty idProperty = new SimpleStringProperty();
    @Transient
    private final StringProperty dateProperty = new SimpleStringProperty();
    @Transient
    private final StringProperty statusProperty = new SimpleStringProperty();
    @Transient
    private final DoubleProperty totalProperty = new SimpleDoubleProperty();
    
    @PostLoad
    public void initializeProperties() {
        idProperty.set(orderId);
        dateProperty.set(orderDate.toString());
        statusProperty.set(status.name());
        totalProperty.set(totalAmount);
    }
    
    public StringProperty idProperty() {
        return idProperty;
    }
    
    public StringProperty dateProperty() {
        return dateProperty;
    }
    
    public StringProperty statusProperty() {
        return statusProperty;
    }
    
    public DoubleProperty totalProperty() {
        return totalProperty;
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