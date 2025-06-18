package com.recursiveMind.WareHouseRecordManagement.service.Impl;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import com.recursiveMind.WareHouseRecordManagement.repository.OrderRepository;
import com.recursiveMind.WareHouseRecordManagement.repository.user.UserOrderRepository;
import com.recursiveMind.WareHouseRecordManagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;

    @Override
    public Order createOrder(Order order) { return orderRepository.save(order); }
    @Override
    public List<Order> getAllOrders() { return orderRepository.findAll(); }
    @Override
    public Optional<Order> getOrderById(Long id) { return orderRepository.findById(id); }
    @Override
    public Order updateOrder(Order order) {
        // Update in admin DB
        Order updated = orderRepository.save(order);
        // Update in user DB
        Order userOrder = userOrderRepository.findByOrderId(order.getOrderId());
        if (userOrder != null) {
            userOrder.setStatus(order.getStatus());
            userOrderRepository.save(userOrder);
        }
        return updated;
    }
    @Override
    public void deleteOrder(Long id) { orderRepository.deleteById(id); }
    @Override
    public Order processOrder(Long orderId) { return null; }
    @Override
    public Order shipOrder(Long orderId) { return null; }
    @Override
    public Order deliverOrder(Long orderId) { return null; }
    @Override
    public Order cancelOrder(Long orderId) { return null; }
    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findAll().stream()
            .filter(o -> o.getOrderDate() != null &&
                !o.getOrderDate().isBefore(startDate) &&
                !o.getOrderDate().isAfter(endDate))
            .toList();
    }
    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findAll().stream()
            .filter(o -> o.getStatus() != null && o.getStatus().name().equalsIgnoreCase(status))
            .toList();
    }
    @Override
    public Order getOrderByOrderId(String orderId) {
        return orderRepository.findAll().stream()
            .filter(o -> o.getOrderId() != null && o.getOrderId().equals(orderId))
            .findFirst().orElse(null);
    }
    @Override
    public long getPendingOrdersCount() {
        return orderRepository.findAll().stream()
            .filter(o -> o.getStatus() != null && o.getStatus().name().equalsIgnoreCase("PENDING"))
            .count();
    }
    @Override
    public long getTodayOrdersCount() {
        LocalDate today = LocalDate.now();
        return orderRepository.findAll().stream()
            .filter(o -> o.getOrderDate() != null && o.getOrderDate().toLocalDate().isEqual(today))
            .count();
    }
    @Override
    public long getMonthlyOrdersCount() {
        YearMonth thisMonth = YearMonth.now();
        return orderRepository.findAll().stream()
            .filter(o -> o.getOrderDate() != null && YearMonth.from(o.getOrderDate()).equals(thisMonth))
            .count();
    }
    @Override
    public Double getMonthlyRevenue() { return 0.0; }
    @Override
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findAll().stream()
            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
            .limit(limit)
            .toList();
    }
    @Override
    public long getTotalOrdersThisMonth() {
        YearMonth thisMonth = YearMonth.now();
        return orderRepository.findAll().stream()
            .filter(o -> o.getOrderDate() != null && YearMonth.from(o.getOrderDate()).equals(thisMonth))
            .count();
    }
}