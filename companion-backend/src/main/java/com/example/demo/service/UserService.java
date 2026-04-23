package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUser(Long userId) {
        return userRepository.findById(userId).orElseGet(() -> {
            User newUser = User.builder().name("Demo User").email("demo@example.com").build();
            return userRepository.save(newUser);
        });
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
