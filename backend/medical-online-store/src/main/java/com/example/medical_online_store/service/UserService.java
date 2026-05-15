package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.LoginRequest;
import com.example.medical_online_store.dto.SignupRequest;
import com.example.medical_online_store.model.User;
import com.example.medical_online_store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser(SignupRequest signupRequest) {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(signupRequest.getEmail());
        if (existingUser.isPresent()) {
            return "Email already exists";
        }

        // Validate password
        if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
            return "Password cannot be empty";
        }

        // Create new user
        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword()); // In real app, hash the password

        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(loginRequest.getPassword())) {
                return "Login successful";
            } else {
                return "Invalid password";
            }
        } else {
            return "User not found";
        }
    }
}
