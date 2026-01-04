package com.HouseRental.controller;

import com.HouseRental.dto.AdminLoginDto;
import com.HouseRental.entity.Property;
import com.HouseRental.entity.User;
import com.HouseRental.service.AdminAuditLogService;
import com.HouseRental.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminAuditLogService adminAuditLogService;

    public AdminController(
            AdminService adminService,
            AdminAuditLogService adminAuditLogService
    ) {
        this.adminService = adminService;
        this.adminAuditLogService = adminAuditLogService;
    }

    
//    admin login 
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(
            @RequestBody AdminLoginDto adminLoginDto
    ) {
        String token = adminService.login(adminLoginDto);
        return ResponseEntity.ok(token);
    }
    
    
    // ================= USERS =================

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<String> enableUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();

        adminService.updateUserStatus(id, true, adminEmail);

        adminAuditLogService.log(
                adminEmail,
                "ENABLE_USER",
                "USER",
                id
        );

        return ResponseEntity.ok("User enabled successfully");
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<String> disableUser(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();

        adminService.updateUserStatus(id, false, adminEmail);

        adminAuditLogService.log(
                adminEmail,
                "DISABLE_USER",
                "USER",
                id
        );

        return ResponseEntity.ok("User disabled successfully");
    }

    // ================= PROPERTIES =================

    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(adminService.getAllProperties());
    }

    @PutMapping("/properties/{id}/enable")
    public ResponseEntity<String> enableProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();

        adminService.updatePropertyStatus(id, true);

        adminAuditLogService.log(
                adminEmail,
                "ENABLE_PROPERTY",
                "PROPERTY",
                id
        );

        return ResponseEntity.ok("Property enabled successfully");
    }

    @PutMapping("/properties/{id}/disable")
    public ResponseEntity<String> disableProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();

        adminService.updatePropertyStatus(id, false);

        adminAuditLogService.log(
                adminEmail,
                "DISABLE_PROPERTY",
                "PROPERTY",
                id
        );

        return ResponseEntity.ok("Property disabled successfully");
    }
}
