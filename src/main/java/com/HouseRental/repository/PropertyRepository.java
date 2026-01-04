package com.HouseRental.repository;

import com.HouseRental.entity.Property;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
	  // ✅ Get properties of a specific landlord
    List<Property> findByOwnerEmail(String ownerEmail);
    
//  find by available and active true
  Page<Property> findByAvailableTrueAndActiveTrue(Pageable pageable);

//    get property based on price range and location
    Page<Property> findByAvailableTrueAndLocationContainingIgnoreCaseAndPriceBetween(
            String location,
            double minPrice,
            double maxPrice,
            Pageable pageable
    );
}
