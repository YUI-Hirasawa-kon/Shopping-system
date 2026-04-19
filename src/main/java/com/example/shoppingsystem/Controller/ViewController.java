package com.example.shoppingsystem.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/auth/login") public String login() { return "auth/login"; }
    @GetMapping("/auth/register") public String register() { return "auth/register"; }
    @GetMapping("/product/list") public String productList() { return "product/list"; }
    @GetMapping("/cart/show") public String cartShow() { return "cart/cart"; }
    @GetMapping("/cart/cart") public String cartPage() { return "cart/cart"; }
    @GetMapping("/order/orders") public String orders() { return "order/order"; }
    @GetMapping("/order/order") public String orderPage() { return "order/order"; }
    @GetMapping("/order/detail") public String orderDetail() { return "order/detail"; }
}
