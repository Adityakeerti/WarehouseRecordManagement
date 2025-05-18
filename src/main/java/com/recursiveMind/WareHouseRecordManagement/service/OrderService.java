package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order updateOrder(Order order);
    void deleteOrder(Long id);
    Order processOrder(Long orderId);
    Order shipOrder(Long orderId);
    Order deliverOrder(Long orderId);
    Order cancelOrder(Long orderId);
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> getOrdersByStatus(String status);
    Order getOrderByOrderId(String orderId);
    long getPendingOrdersCount();
    long getTodayOrdersCount();
    long getMonthlyOrdersCount();
    Double getMonthlyRevenue();
    List<Order> getRecentOrders(int limit);
} 