package dev.quantumentangled.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(String username, String email, String rawPassword, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        return userRepository.save(user);
    }
}