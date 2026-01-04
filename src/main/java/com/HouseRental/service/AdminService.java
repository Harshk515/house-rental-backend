package com.HouseRental.service;

import com.HouseRental.dto.AdminLoginDto;
import com.HouseRental.dto.LoginRequest;
import com.HouseRental.entity.Property;
import com.HouseRental.entity.Role;
import com.HouseRental.entity.User;
import com.HouseRental.repository.PropertyRepository;
import com.HouseRental.repository.UserRepository;
import com.HouseRental.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AdminService(
            UserRepository userRepository,
            PropertyRepository propertyRepository,PasswordEncoder passwordEncoder,
             JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
        
    }
    
    
//    admin login business logic
    public String login(AdminLoginDto adminLoginDto) {

        User user = userRepository.findByEmail(adminLoginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
     
        if (!passwordEncoder.matches(adminLoginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        Role r = user.getRole();
        
       if((r==Role.LANDLORD)|| (r==Role.TENANT)) {
    	   throw new RuntimeException("Only Admin can Login");
       }
        
        // 👇 role added to JWT
        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }


    
    

    // ================= USERS =================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserStatus(Long userId, boolean active, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(active);
        userRepository.save(user);

        // NOTE:
        // Audit logging is intentionally NOT here.
        // Controller handles logging so it knows WHO (adminEmail).
    }

    // ================= PROPERTIES =================

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public void updatePropertyStatus(Long propertyId, boolean active) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setActive(active);
        propertyRepository.save(property);
    }
}
