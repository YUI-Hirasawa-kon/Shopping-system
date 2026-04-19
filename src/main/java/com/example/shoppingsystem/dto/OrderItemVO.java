package com.example.shoppingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
