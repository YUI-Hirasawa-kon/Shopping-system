package com.example.shoppingsystem.Controller;

import com.example.shoppingsystem.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.success("Hello, " + username);
    }
}