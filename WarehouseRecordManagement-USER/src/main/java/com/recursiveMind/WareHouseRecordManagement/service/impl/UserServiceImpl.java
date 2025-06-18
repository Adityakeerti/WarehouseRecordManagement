package com.recursiveMind.WareHouseRecordManagement.service.impl;

import com.recursiveMind.WareHouseRecordManagement.model.User;
import com.recursiveMind.WareHouseRecordManagement.repository.user.UserRepository;
import com.recursiveMind.WareHouseRecordManagement.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User authenticate(String username, String password) {
        System.out.println("=== DEBUG: Authentication attempt ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        User user = userRepository.findByUsername(username);
        System.out.println("User found: " + (user != null));
        
        if (user != null) {
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Password match: " + password.equals(user.getPassword()));
        }
        
        if (user != null && password.equals(user.getPassword())) {
            System.out.println("Authentication successful!");
            return user;
        }
        
        System.out.println("Authentication failed!");
        return null;
    }
    
    @Override
    public User createUser(User user) {
        // Don't hash password since we're using NoOpPasswordEncoder
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        // Don't hash password since we're using NoOpPasswordEncoder
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 