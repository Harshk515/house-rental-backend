package com.HouseRental.controller;

import com.HouseRental.dto.PropertyRequest;
import com.HouseRental.entity.Property;
import com.HouseRental.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Add property (JWT required)
    @PostMapping
    public ResponseEntity<Property> addProperty(
            @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(
                propertyService.addProperty(request, ownerEmail)
        );
    
    }
    // Get all properties
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // Get property by id
    @GetMapping("/{id}")
    public ResponseEntity<Property> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

//    delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        propertyService.deleteProperty(id, authentication.getName());
        return ResponseEntity.ok("Property deleted successfully");
    }

//    get my property
    @GetMapping("/my")
    public ResponseEntity<List<Property>> getMyProperties(Authentication authentication) {
        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(
                propertyService.getMyProperties(ownerEmail)
        );
    }
   
//    update property 
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(
                propertyService.updateProperty(id, request, ownerEmail)
        );
    }  
    
//    available property pagination
    @GetMapping("/available")
    public ResponseEntity<Page<Property>> getAvailableProperties(Pageable pageable) {
        return ResponseEntity.ok(propertyService.getAvailableProperties(pageable));
    }


    
//    search property
    @GetMapping("/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam String location,
            @RequestParam double minPrice,
            @RequestParam double maxPrice,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                propertyService.searchProperties(location, minPrice, maxPrice, pageable)
        );
    }



    
}
