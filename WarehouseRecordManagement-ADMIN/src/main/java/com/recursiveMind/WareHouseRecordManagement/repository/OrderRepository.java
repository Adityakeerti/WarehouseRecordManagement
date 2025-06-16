package com.recursiveMind.WareHouseRecordManagement.repository;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findTop10ByOrderByOrderDateDesc();
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    long countTodayOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) AND MONTH(o.orderDate) = MONTH(CURRENT_DATE)")
    long countMonthlyOrders();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) AND MONTH(o.orderDate) = MONTH(CURRENT_DATE)")
    Double getMonthlyRevenue();
    
    @Query("SELECT o FROM Order o WHERE o.orderId = ?1")
    Order findByOrderId(String orderId);

    long countByStatus(OrderStatus status);

    long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetween(LocalDateTime startDate, LocalDateTime endDate);
} 