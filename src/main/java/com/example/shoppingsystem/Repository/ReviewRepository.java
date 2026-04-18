package com.example.shoppingsystem.Repository;


import com.example.shoppingsystem.Entity.Review;
import com.example.shoppingsystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreateTimeDesc(Product product);
    boolean existsByOrderIdAndProductId(Long orderId, Long productId); // 检查用户是否已评论过该商品
}
