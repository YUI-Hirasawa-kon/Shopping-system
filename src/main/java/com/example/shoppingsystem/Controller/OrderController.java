package com.example.shoppingsystem.Controller;

import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Service.OrderService;
import com.example.shoppingsystem.Service.UserContextService;
import com.example.shoppingsystem.dto.ApiResponse;
import com.example.shoppingsystem.dto.CreateOrderRequest;
import com.example.shoppingsystem.dto.OrderDetailVO;
import com.example.shoppingsystem.dto.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserContextService userContextService;

    @PostMapping
    public ApiResponse<?> createOrder(@RequestBody(required = false) CreateOrderRequest request) {
        try {
            User currentUser = userContextService.getCurrentUser();
            String address = request == null ? null : request.getAddress();
            OrderVO orderVO = orderService.createOrderFromCart(currentUser, address);
            return ApiResponse.success(orderVO);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/{orderId}/pay")
    public ApiResponse<?> payOrder(@PathVariable Long orderId) {
        try {
            User currentUser = userContextService.getCurrentUser();
            OrderService.PayResult payResult = orderService.payOrder(orderId, currentUser);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("orderId", payResult.getOrderId());
            data.put("status", payResult.getStatus());
            data.put("payTime", payResult.getPayTime());
            return ApiResponse.success("Mock payment successful", data);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IllegalStateException e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<OrderVO>> getOrderList() {
        User currentUser = userContextService.getCurrentUser();
        return ApiResponse.success(orderService.getOrdersByUser(currentUser));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<?> getOrderDetail(@PathVariable Long orderId) {
        try {
            User currentUser = userContextService.getCurrentUser();
            OrderDetailVO detailVO = orderService.getOrderDetail(orderId, currentUser);
            return ApiResponse.success(detailVO);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
}
