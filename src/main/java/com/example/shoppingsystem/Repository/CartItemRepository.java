package com.example.shoppingsystem.Repository;

import com.example.shoppingsystem.Entity.CartItem;
import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
}