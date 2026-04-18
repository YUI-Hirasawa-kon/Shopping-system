package com.example.shoppingsystem.Controller;
import com.example.shoppingsystem.dto.*;
import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Repository.UserRepository;
import com.example.shoppingsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 注册接口
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error(400, "Username already exists.");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER"); // Default role

        userRepository.save(user);

        return ApiResponse.success("Registration successful", null);
    }

    // 登录接口
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            return ApiResponse.error(401, "Username or password incorrect.");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error(401, "Username or password incorrect.");
        }

        // 生成JWT
        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRole());

        return ApiResponse.success(response);
    }
}
