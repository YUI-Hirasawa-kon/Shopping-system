package com.example.shoppingsystem.dto;

import lombok.Data;

@Data
public class CartUpdateRequest {
    private Long productId;
    private Integer quantity;
}
