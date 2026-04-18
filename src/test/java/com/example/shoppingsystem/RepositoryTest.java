package com.example.shoppingsystem;


import com.example.shoppingsystem.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindUser() {
        userRepository.findByUsername("user1").ifPresent(user -> {
            System.out.println(user.getUsername() + " - " + user.getEmail());
        });
    }
}
