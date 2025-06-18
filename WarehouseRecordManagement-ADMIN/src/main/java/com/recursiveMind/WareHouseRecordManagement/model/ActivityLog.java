package com.recursiveMind.WareHouseRecordManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action")
    private String action; // ADD, UPDATE, DELETE
    
    @Column(name = "product_code")
    private String productCode;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "details")
    private String details;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
} 