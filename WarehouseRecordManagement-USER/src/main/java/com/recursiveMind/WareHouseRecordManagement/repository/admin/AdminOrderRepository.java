package com.recursiveMind.WareHouseRecordManagement.repository.admin;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Order findByOrderId(String orderId);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findTop10ByOrderByOrderDateDesc();
} 