package com.HouseRental.controller;

import com.HouseRental.entity.BookingRequest;
import com.HouseRental.service.BookingRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    public BookingRequestController(BookingRequestService bookingRequestService) {
        this.bookingRequestService = bookingRequestService;
    }

    // TENANT: send booking request
    @PostMapping("/{propertyId}")
    public ResponseEntity<BookingRequest> sendRequest(
            @PathVariable Long propertyId,
            @RequestBody(required = false) String message,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingRequestService.createRequest(
                        propertyId,
                        authentication.getName(),
                        message
                )
        );
    }

    // LANDLORD: view requests
    @GetMapping("/my")
    public ResponseEntity<List<BookingRequest>> getMyRequests(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingRequestService.getRequestsForLandlord(
                        authentication.getName()
                )
        );
    }

    // LANDLORD: approve
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        bookingRequestService.approveRequest(id, authentication.getName());
        return ResponseEntity.ok("Booking request approved");
    }

    // LANDLORD: reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        bookingRequestService.rejectRequest(id, authentication.getName());
        return ResponseEntity.ok("Booking request rejected");
    }
    
 // TENANT: view my booking history  
    @GetMapping("/my-history")
    public ResponseEntity<List<BookingRequest>> myBookingHistory(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookingRequestService.getRequestsForTenant(
                        authentication.getName()
                )
        );
    }

//    cancel booking request 
    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelBookingRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String tenantEmail = authentication.getName();

        bookingRequestService.cancelRequest(id, tenantEmail);

        return ResponseEntity.ok("Booking request cancelled successfully");
    }

    
    
}
