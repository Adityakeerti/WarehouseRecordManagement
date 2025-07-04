package com.recursiveMind.WareHouseRecordManagement.repository.user;

import com.recursiveMind.WareHouseRecordManagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(String orderId);
} 