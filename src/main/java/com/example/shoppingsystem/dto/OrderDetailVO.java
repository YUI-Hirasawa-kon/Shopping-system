package com.example.shoppingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;
    private String address;
    private String createTime;
    private String payTime;
    private List<OrderItemVO> items;
    private LogisticsVO logistics;
}
