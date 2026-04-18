package com.example.shoppingsystem.Repository;

import com.example.shoppingsystem.Entity.OrderItem;
import com.example.shoppingsystem.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}