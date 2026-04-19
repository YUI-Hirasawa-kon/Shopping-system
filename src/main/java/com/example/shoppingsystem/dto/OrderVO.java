package com.example.shoppingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;
    private String createTime;
}
