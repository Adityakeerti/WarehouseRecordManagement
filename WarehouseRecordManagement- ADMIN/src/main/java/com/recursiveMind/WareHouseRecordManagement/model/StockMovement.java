package com.recursiveMind.WareHouseRecordManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Enumerated(EnumType.STRING)
    private MovementType type;
    
    private int quantity;
    
    private String reference;  // Order number or other reference
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User performedBy;
    
    private LocalDateTime movementDate;
    
    private String notes;
    
    public enum MovementType {
        IN,     // Stock received
        OUT,    // Stock issued
        ADJUST  // Stock adjustment
    }
    
    @PrePersist
    protected void onCreate() {
        movementDate = LocalDateTime.now();
    }
} 