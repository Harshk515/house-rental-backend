package com.HouseRental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_requests")
@Getter
@Setter
public class BookingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Property being requested
    private Long propertyId;

    // Tenant who requested
    private String tenantEmail;

    // Owner of the property
    private String landlordEmail;

    // PENDING / APPROVED / REJECTED
    private String status;

    // Optional message
    private String message;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
