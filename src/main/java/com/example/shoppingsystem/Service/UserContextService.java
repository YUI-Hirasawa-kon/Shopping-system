package com.example.shoppingsystem.Service;

import com.example.shoppingsystem.Entity.User;
import com.example.shoppingsystem.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not logged in");
        }

        String username = authentication.getName();
        if (username == null || username.trim().isEmpty() || "anonymousUser".equals(username)) {
            throw new IllegalStateException("User is not logged in");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user does not exist"));
    }
}
