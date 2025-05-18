package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.*;
import com.recursiveMind.WareHouseRecordManagement.repository.OrderRepository;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import com.recursiveMind.WareHouseRecordManagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

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
    public Order processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check stock availability
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        // Update stock levels
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            productService.removeStock(product.getId(), item.getQuantity());
        }
        
        order.setStatus(OrderStatus.PROCESSING);
        return orderRepository.save(order);
    }

    @Override
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Order must be in PROCESSING status to be shipped");
        }
        
        order.setStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }

    @Override
    public Order deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Order must be in SHIPPED status to be delivered");
        }
        
        order.setStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel a delivered order");
        }
        
        // Restore stock levels if order was processing
        if (order.getStatus() == OrderStatus.PROCESSING) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                productService.addStock(product.getId(), item.getQuantity());
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        List<Order> orders = orderRepository.findAll();
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
        return orders.stream().limit(limit).collect(Collectors.toList());
    }

    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(OrderStatus.valueOf(status.toUpperCase()));
    }

    @Override
    public Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public long getPendingOrdersCount() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    @Override
    public long getTodayOrdersCount() {
        return orderRepository.countTodayOrders();
    }

    @Override
    public long getMonthlyOrdersCount() {
        return orderRepository.countMonthlyOrders();
    }

    @Override
    public Double getMonthlyRevenue() {
        return orderRepository.getMonthlyRevenue();
    }
} 