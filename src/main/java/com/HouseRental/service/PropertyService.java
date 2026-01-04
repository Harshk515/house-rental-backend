package com.HouseRental.service;

import com.HouseRental.dto.PropertyRequest;
import com.HouseRental.entity.Property;
import com.HouseRental.repository.PropertyRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Add property
    public Property addProperty(PropertyRequest request, String ownerEmail) {

        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPrice(request.getPrice());
        property.setLocation(request.getLocation());
        property.setImageUrl(request.getImageUrl());
        property.setOwnerEmail(ownerEmail);

        return propertyRepository.save(property);
    }
    
//    update properties
    public Property updateProperty(
            Long propertyId,
            PropertyRequest request,
            String ownerEmail
    ) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // 🔒 OWNER CHECK
        if (!property.getOwnerEmail().equals(ownerEmail)) {
            throw new RuntimeException("You are not allowed to update this property");
        }

        // ✅ Update fields
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPrice(request.getPrice());
        property.setLocation(request.getLocation());
        property.setImageUrl(request.getImageUrl());

        return propertyRepository.save(property);
    }
    
    // Get all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Get property by id
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

//    delete property landlord only
    public void deleteProperty(Long id, String ownerEmail) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getOwnerEmail().equals(ownerEmail)) {
            throw new RuntimeException("You are not allowed to delete this property");
        }

        propertyRepository.delete(property);
    }
 
 // ✅ LANDLORD dashboard
    public List<Property> getMyProperties(String ownerEmail) {
        return propertyRepository.findByOwnerEmail(ownerEmail);
    }
    
//    get available properties pagination
    public Page<Property> getAvailableProperties(Pageable pageable) {
        return propertyRepository.findByAvailableTrueAndActiveTrue(pageable);
    }

//    search property based on price range
    public Page<Property> searchProperties(
            String location,
            double minPrice,
            double maxPrice,
            Pageable pageable
    ) {
        return propertyRepository
                .findByAvailableTrueAndLocationContainingIgnoreCaseAndPriceBetween(
                        location,
                        minPrice,
                        maxPrice,
                        pageable
                );
    }
    
}
