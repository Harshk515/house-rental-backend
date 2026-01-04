package com.HouseRental.service;

import com.HouseRental.dto.RegisterRequest;
import com.HouseRental.entity.Role;
import com.HouseRental.entity.User;
import com.HouseRental.exception.AdminRegistrationNotAllowedException;
import com.HouseRental.repository.UserRepository;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.HouseRental.dto.LoginRequest;
import com.HouseRental.security.JwtUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() == Role.ADMIN) {
            throw new AdminRegistrationNotAllowedException("Admin registration is not allowed");
        }	

        user.setRole(
            request.getRole() == null ? Role.TENANT : request.getRole()
        );

        return userRepository.save(user);
    }
    
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        	
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin login is not allowed");
        }
        
        // 👇 role added to JWT
        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
                  
}
