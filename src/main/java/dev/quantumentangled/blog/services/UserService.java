package dev.quantumentangled.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.exceptions.UsernameAlreadyExistsException;
import dev.quantumentangled.blog.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(String username, String email, String fullName) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void completeProfile(Long userId, String username) 
            throws UsernameAlreadyExistsException {
        
        if (isUsernameTaken(username)) {
            throw new UsernameAlreadyExistsException("Username is already taken: " + username);
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        user.setUsername(username);
        
        userRepository.save(user);
    }
}