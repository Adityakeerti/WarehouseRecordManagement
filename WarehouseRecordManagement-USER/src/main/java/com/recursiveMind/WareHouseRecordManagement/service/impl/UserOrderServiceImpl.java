package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.*;
import com.recursiveMind.WareHouseRecordManagement.repository.user.UserOrderRepository;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("userOrderService")
@Transactional("userTransactionManager")
public class UserOrderServiceImpl implements OrderService {

    @Autowired
    private UserOrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        order.setOrderId(generateOrderId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    @Override
    public Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 