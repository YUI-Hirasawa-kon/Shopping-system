package com.example.shoppingsystem.Repository;


import com.example.shoppingsystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
    Page<Product> findByStatus(Integer status, Pageable pageable);
}
