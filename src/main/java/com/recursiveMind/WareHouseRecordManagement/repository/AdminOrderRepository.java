package com.recursiveMind.WareHouseRecordManagement.repository;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(String orderId);
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByStatus(OrderStatus status);
    long countByStatus(OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    long countTodayOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE MONTH(o.orderDate) = MONTH(CURRENT_DATE) AND YEAR(o.orderDate) = YEAR(CURRENT_DATE)")
    long countMonthlyOrders();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE MONTH(o.orderDate) = MONTH(CURRENT_DATE) AND YEAR(o.orderDate) = YEAR(CURRENT_DATE)")
    Double getMonthlyRevenue();

    long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
} 