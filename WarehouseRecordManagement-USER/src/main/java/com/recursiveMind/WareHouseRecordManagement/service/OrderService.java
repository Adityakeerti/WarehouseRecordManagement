package com.recursiveMind.WareHouseRecordManagement.service;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order updateOrder(Order order);
    void deleteOrder(Long id);
<<<<<<< HEAD
    List<Order> getOrdersByUserId(Long userId);
=======
>>>>>>> 5c4f977 (Done)
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Order getOrderByOrderId(String orderId);
    List<Order> getRecentOrders(int limit);
} 