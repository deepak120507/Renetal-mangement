package com.rentalhouse.controller;

import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.entity.DomainEnums.PropertyType;
import com.rentalhouse.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyService properties;
    public PropertyController(PropertyService properties) { this.properties = properties; }

    @GetMapping
    public List<PropertyView> all() { return properties.publicListings().stream().map(PropertyView::from).toList(); }

    @GetMapping("/mine")
    public List<PropertyView> mine(Authentication auth) { return properties.mine(auth.getName()).stream().map(PropertyView::from).toList(); }

    @GetMapping("/search")
    public List<PropertyView> search(@RequestParam(required = false) String location,
                                     @RequestParam(required = false) PropertyType propertyType,
                                     @RequestParam(required = false) BigDecimal minRent,
                                     @RequestParam(required = false) BigDecimal maxRent) {
        return properties.search(location, propertyType, minRent, maxRent).stream().map(PropertyView::from).toList();
    }

    @GetMapping("/location/{location}")
    public List<PropertyView> byLocation(@PathVariable String location) {
        return properties.search(location, null, null, null).stream().map(PropertyView::from).toList();
    }

    @GetMapping("/type/{type}")
    public List<PropertyView> byType(@PathVariable PropertyType type) {
        return properties.search(null, type, null, null).stream().map(PropertyView::from).toList();
    }

    @GetMapping("/{id}")
    public PropertyView get(@PathVariable Long id) { return PropertyView.from(properties.find(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(@Valid @RequestBody PropertyRequest request, Authentication auth) {
        return new ApiResponse(true, "Property created", PropertyView.from(properties.create(request, auth.getName())));
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody PropertyRequest request, Authentication auth) {
        return new ApiResponse(true, "Property updated", PropertyView.from(properties.update(id, request, auth.getName())));
    }

    @PutMapping("/{id}/availability")
    public ApiResponse availability(@PathVariable Long id, @RequestParam boolean available, Authentication auth) {
        return new ApiResponse(true, "Availability updated", PropertyView.from(properties.availability(id, available, auth.getName())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id, Authentication auth) {
        properties.delete(id, auth.getName());
        return new ApiResponse(true, "Property deleted", null);
    }
}