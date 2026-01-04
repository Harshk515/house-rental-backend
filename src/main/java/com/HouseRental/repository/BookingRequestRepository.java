package com.HouseRental.repository;

import com.HouseRental.entity.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository
        extends JpaRepository<BookingRequest, Long> {

    List<BookingRequest> findByLandlordEmail(String landlordEmail);

    List<BookingRequest> findByTenantEmail(String tenantEmail);

    List<BookingRequest> findByPropertyIdAndStatus(
            Long propertyId,
            String status
    );
    
    boolean existsByPropertyIdAndTenantEmailAndStatus(
            Long propertyId,
            String tenantEmail,
            String status
    );

}
