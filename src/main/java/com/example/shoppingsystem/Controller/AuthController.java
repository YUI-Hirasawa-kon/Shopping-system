package com.example.shoppingsystem.Controller;
import com.example.shoppingsystem.dto.*;
import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Repository.UserRepository;
import com.example.shoppingsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register API
    @PostMapping("/register")
    @ResponseBody
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        // Check whether username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error(400, "Username already exists.");
        }

        // Create a new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER"); // Default role

        userRepository.save(user);

        return ApiResponse.success("Registration successful", null);
    }

        // Login page (GET)
        @GetMapping("/login")
        public String loginPage() {
            return "auth/login";
        }

        // Register page (GET)
        @GetMapping("/register")
        public String registerPage() {
            return "auth/register";
        }

    // Login API
    @PostMapping("/login")
    @ResponseBody
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            return ApiResponse.error(401, "Username or password incorrect.");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error(401, "Username or password incorrect.");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getUsername());
        LoginResponse response = new LoginResponse(token, user.getUsername(), user.getRole());

        return ApiResponse.success(response);
    }
}
