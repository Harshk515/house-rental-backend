package com.HouseRental.service;

import com.HouseRental.entity.BookingRequest;
import com.HouseRental.entity.Property;
import com.HouseRental.exception.DuplicateBookingException;
import com.HouseRental.repository.BookingRequestRepository;
import com.HouseRental.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final PropertyRepository propertyRepository;

    public BookingRequestService(
            BookingRequestRepository bookingRequestRepository,
            PropertyRepository propertyRepository
    ) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.propertyRepository = propertyRepository;
    }

    // TENANT: create booking request
    public BookingRequest createRequest(
            Long propertyId,
            String tenantEmail,
            String message
    ) 
    {
    	
    	// prevent duplicate pending request
    	boolean exists =
    	    bookingRequestRepository
    	        .existsByPropertyIdAndTenantEmailAndStatus(
    	            propertyId,
    	            tenantEmail,
    	            "PENDING"
    	        );

    	if (exists) {
    	    throw new DuplicateBookingException(
    	        "You already have a pending request for this property"
    	    );
    	}

    	else {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        BookingRequest request = new BookingRequest();
        request.setPropertyId(propertyId);
        request.setTenantEmail(tenantEmail);
        request.setLandlordEmail(property.getOwnerEmail());
        request.setStatus("PENDING");
        request.setMessage(message);

        return bookingRequestRepository.save(request);
    }
    }

    // LANDLORD: view requests
    public List<BookingRequest> getRequestsForLandlord(String landlordEmail) {
        return bookingRequestRepository.findByLandlordEmail(landlordEmail);
    }

    // LANDLORD: approve request
    public void approveRequest(Long requestId, String landlordEmail) {

        BookingRequest request = bookingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getLandlordEmail().equals(landlordEmail)) {
            throw new RuntimeException("Unauthorized");
        }	

        // approve selected request
        request.setStatus("APPROVED");
        bookingRequestRepository.save(request);

        // mark property unavailable
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setAvailable(false);
        propertyRepository.save(property);

        // reject other pending requests for same property
        List<BookingRequest> pendingRequests =
                bookingRequestRepository.findByPropertyIdAndStatus(
                        request.getPropertyId(),
                        "PENDING"
                );

        for (BookingRequest r : pendingRequests) {
            r.setStatus("REJECTED");
        }

        bookingRequestRepository.saveAll(pendingRequests);
    }

    // LANDLORD: reject request
    public void rejectRequest(Long requestId, String landlordEmail) {

        BookingRequest request = bookingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getLandlordEmail().equals(landlordEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        request.setStatus("REJECTED");
        bookingRequestRepository.save(request);
    }
    
 // TENANT: view my booking requests
    public List<BookingRequest> getRequestsForTenant(String tenantEmail) {
        return bookingRequestRepository.findByTenantEmail(tenantEmail);
    }
    
    
    
//    Tenant: cancel my booking request
    public void cancelRequest(Long requestId, String tenantEmail) {

        BookingRequest request = bookingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found"));

        // 🔐 Ensure tenant owns this request
        if (!request.getTenantEmail().equals(tenantEmail)) {
            throw new RuntimeException("You are not allowed to cancel this request");
        }

        // ❌ Only pending requests can be cancelled
        if (!request.getStatus().equals("PENDING")) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        request.setStatus("CANCELLED");
        bookingRequestRepository.save(request);
    }


}
