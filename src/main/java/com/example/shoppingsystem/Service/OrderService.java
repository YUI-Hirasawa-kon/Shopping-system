package com.example.shoppingsystem.Service;

import com.example.shoppingsystem.Entity.CartItem;
import com.example.shoppingsystem.Entity.Order;
import com.example.shoppingsystem.Entity.OrderItem;
import com.example.shoppingsystem.Entity.Product;
import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Repository.CartItemRepository;
import com.example.shoppingsystem.Repository.OrderItemRepository;
import com.example.shoppingsystem.Repository.OrderRepository;
import com.example.shoppingsystem.Repository.ProductRepository;
import com.example.shoppingsystem.dto.LogisticsVO;
import com.example.shoppingsystem.dto.OrderDetailVO;
import com.example.shoppingsystem.dto.OrderItemVO;
import com.example.shoppingsystem.dto.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Integer PRODUCT_ON_SHELF = 1;
    private static final String ORDER_STATUS_PENDING = "PENDING_PAYMENT";
    private static final String ORDER_STATUS_PAID = "PAID";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final double PAY_SUCCESS_RATE = 0.8D;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderVO createOrderFromCart(User user, String address) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot place order.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new IllegalArgumentException("Invalid cart item data.");
            }
            if (!PRODUCT_ON_SHELF.equals(product.getStatus())) {
                throw new IllegalArgumentException("Product is off shelf: " + product.getName());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(ORDER_STATUS_PENDING);
        order.setAddress(address == null || address.trim().isEmpty() ? "Not provided" : address.trim());
        order.setCreateTime(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteByUser(user);
        return toOrderVO(savedOrder);
    }

    @Transactional
    public PayResult payOrder(Long orderId, User user) {
        Order order = getOrderByIdAndUser(orderId, user);
        if (!ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new IllegalArgumentException("Order status is not pending payment.");
        }

        // For deterministic demo behavior, set success=true; random logic is kept for mock payment.
        boolean success = new Random().nextDouble() < PAY_SUCCESS_RATE;
        if (!success) {
            throw new IllegalStateException("Mock payment failed: insufficient balance.");
        }

        order.setStatus(ORDER_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        return new PayResult(savedOrder.getId(), savedOrder.getStatus(), formatDateTime(savedOrder.getPayTime()));
    }

    public List<OrderVO> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreateTimeDesc(user)
                .stream()
                .map(this::toOrderVO)
                .collect(Collectors.toList());
    }

    public OrderDetailVO getOrderDetail(Long orderId, User user) {
        Order order = getOrderByIdAndUser(orderId, user);
        List<OrderItemVO> items = orderItemRepository.findByOrder(order)
                .stream()
                .map(item -> new OrderItemVO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        LogisticsVO logistics = null;
        if (ORDER_STATUS_PAID.equals(order.getStatus())) {
            logistics = buildMockLogistics(order);
        }

        return new OrderDetailVO(
                order.getId(),
                order.getOrderNo(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getAddress(),
                formatDateTime(order.getCreateTime()),
                formatDateTime(order.getPayTime()),
                items,
                logistics
        );
    }

    private Order getOrderByIdAndUser(Long orderId, User user) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new IllegalArgumentException("Order not found.");
        }

        Order order = optionalOrder.get();
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No permission to access this order.");
        }
        return order;
    }

    private LogisticsVO buildMockLogistics(Order order) {
        List<LogisticsVO.TraceVO> traces = new ArrayList<>();
        LocalDateTime baseTime = order.getPayTime() == null ? LocalDateTime.now() : order.getPayTime();
        traces.add(new LogisticsVO.TraceVO(formatDateTime(baseTime.plusHours(1)), "Your order has been packed."));
        traces.add(new LogisticsVO.TraceVO(formatDateTime(baseTime.plusHours(3)), "Courier has picked up the package."));
        return new LogisticsVO("Mock Express", "SF" + order.getId(), "Shipped", traces);
    }

    private OrderVO toOrderVO(Order order) {
        return new OrderVO(
                order.getId(),
                order.getOrderNo(),
                order.getTotalAmount(),
                order.getStatus(),
                formatDateTime(order.getCreateTime())
        );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATE_TIME_FORMATTER);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return timestamp + randomSuffix;
    }

    public static class PayResult {
        private final Long orderId;
        private final String status;
        private final String payTime;

        public PayResult(Long orderId, String status, String payTime) {
            this.orderId = orderId;
            this.status = status;
            this.payTime = payTime;
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getStatus() {
            return status;
        }

        public String getPayTime() {
            return payTime;
        }
    }
}
