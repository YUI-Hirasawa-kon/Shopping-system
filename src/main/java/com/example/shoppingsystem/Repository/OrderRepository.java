package com.example.shoppingsystem.Repository;

import com.example.shoppingsystem.Entity.Order;
import com.example.shoppingsystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreateTimeDesc(User user);
    Order findByOrderNo(String orderNo);
}
