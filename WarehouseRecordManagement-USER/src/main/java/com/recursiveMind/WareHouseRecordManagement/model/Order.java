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
    
<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
=======
>>>>>>> 5c4f977 (Done)
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
<<<<<<< HEAD
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
=======
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
>>>>>>> 5c4f977 (Done)
    
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
    
<<<<<<< HEAD
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { 
        this.orderId = orderId;
        this.idProperty.set(orderId);
    }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { 
        this.orderDate = orderDate;
        this.dateProperty.set(orderDate.toString());
    }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { 
        this.status = status;
        this.statusProperty.set(status.getDisplayName());
    }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { 
        this.totalAmount = totalAmount;
        this.totalProperty.set(totalAmount);
    }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // JavaFX Property getters
    public StringProperty idProperty() { return idProperty; }
    public StringProperty dateProperty() { return dateProperty; }
    public StringProperty statusProperty() { return statusProperty; }
    public DoubleProperty totalProperty() { return totalProperty; }
    
=======
>>>>>>> 5c4f977 (Done)
    @PostLoad
    public void initializeProperties() {
        idProperty.set(orderId);
        dateProperty.set(orderDate.toString());
<<<<<<< HEAD
        statusProperty.set(status.getDisplayName());
        totalProperty.set(totalAmount);
    }
    
=======
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
    
>>>>>>> 5c4f977 (Done)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
<<<<<<< HEAD
    
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        calculateTotal();
    }
    
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        calculateTotal();
    }
    
    public void calculateTotal() {
        this.totalAmount = orderItems.stream()
            .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
            .sum();
        totalProperty.set(totalAmount);
    }
=======
>>>>>>> 5c4f977 (Done)
} 