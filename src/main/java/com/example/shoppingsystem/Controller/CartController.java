package com.example.shoppingsystem.Controller;

import com.example.shoppingsystem.Entity.CartItem;
import com.example.shoppingsystem.Entity.Product;
import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Repository.CartItemRepository;
import com.example.shoppingsystem.Repository.ProductRepository;
import com.example.shoppingsystem.Service.UserContextService;
import com.example.shoppingsystem.dto.ApiResponse;
import com.example.shoppingsystem.dto.CartAddRequest;
import com.example.shoppingsystem.dto.CartUpdateRequest;
import com.example.shoppingsystem.dto.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Integer PRODUCT_ON_SHELF = 1;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserContextService userContextService;

    @GetMapping
    public ApiResponse<CartVO> getCurrentUserCart() {
        User currentUser = userContextService.getCurrentUser();
        return ApiResponse.success(buildCartVO(currentUser));
    }

    @PostMapping("/add")
    public ApiResponse<?> addToCart(@RequestBody CartAddRequest request) {
        if (request.getProductId() == null) {
            return ApiResponse.error(400, "Product ID cannot be null");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ApiResponse.error(400, "Quantity must be greater than 0");
        }

        User currentUser = userContextService.getCurrentUser();
        Optional<Product> optionalProduct = productRepository.findById(request.getProductId());
        if (optionalProduct.isEmpty() || !PRODUCT_ON_SHELF.equals(optionalProduct.get().getStatus())) {
            return ApiResponse.error(404, "Product not found or off shelf");
        }

        Product product = optionalProduct.get();
        Optional<CartItem> optionalCartItem = cartItemRepository.findByUserAndProduct(currentUser, product);
        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(currentUser);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemRepository.save(cartItem);
        }

        return ApiResponse.success(buildCartVO(currentUser));
    }

    @PutMapping("/update")
    public ApiResponse<?> updateCartItem(@RequestBody CartUpdateRequest request) {
        if (request.getProductId() == null) {
            return ApiResponse.error(400, "Product ID cannot be null");
        }
        if (request.getQuantity() == null || request.getQuantity() < 1) {
            return ApiResponse.error(400, "Quantity must be at least 1");
        }

        User currentUser = userContextService.getCurrentUser();
        Optional<Product> optionalProduct = productRepository.findById(request.getProductId());
        if (optionalProduct.isEmpty()) {
            return ApiResponse.error(404, "Product not found");
        }

        Product product = optionalProduct.get();
        Optional<CartItem> optionalCartItem = cartItemRepository.findByUserAndProduct(currentUser, product);
        if (optionalCartItem.isEmpty()) {
            return ApiResponse.error(404, "Product does not exist in cart");
        }

        CartItem cartItem = optionalCartItem.get();
        cartItem.setQuantity(request.getQuantity());
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        return ApiResponse.success(buildCartVO(currentUser));
    }

    @DeleteMapping("/remove/{productId}")
    public ApiResponse<?> removeCartItem(@PathVariable Long productId) {
        User currentUser = userContextService.getCurrentUser();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return ApiResponse.error(404, "Product not found");
        }

        Product product = optionalProduct.get();
        Optional<CartItem> optionalCartItem = cartItemRepository.findByUserAndProduct(currentUser, product);
        if (optionalCartItem.isEmpty()) {
            return ApiResponse.error(404, "Product does not exist in cart");
        }

        cartItemRepository.delete(optionalCartItem.get());
        return ApiResponse.success(buildCartVO(currentUser));
    }

    private CartVO buildCartVO(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            return new CartVO(new ArrayList<>(), BigDecimal.ZERO);
        }

        List<CartVO.CartItemVO> itemVOList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            CartVO.CartItemVO itemVO = new CartVO.CartItemVO(
                    product.getId(),
                    product.getName(),
                    cartItem.getQuantity(),
                    price,
                    subtotal
            );
            itemVOList.add(itemVO);
        }

        return new CartVO(itemVOList, totalPrice);
    }
}
