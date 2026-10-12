package com.rentalhouse.service;

import com.rentalhouse.dto.ApiDtos.PropertyRequest;
import com.rentalhouse.entity.DomainEnums.PropertyType;
import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.entity.Property;
import com.rentalhouse.entity.User;
import com.rentalhouse.repository.PropertyRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.rentalhouse.exception.AppExceptions.ResourceNotFoundException;

@Service
public class PropertyService {
    private final PropertyRepository properties;
    private final UserService users;

    public PropertyService(PropertyRepository properties, UserService users) { this.properties = properties; this.users = users; }
    public List<Property> publicListings() { return properties.findByAvailabilityTrueOrderByIdDesc(); }
    public List<Property> all() { return properties.findAll(); }
    public List<Property> mine(String email) { return properties.findByOwnerIdOrderByIdDesc(users.findByEmail(email).getId()); }
    public Property find(Long id) { return properties.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found")); }

    public List<Property> search(String location, PropertyType type, BigDecimal minRent, BigDecimal maxRent) {
        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> filters = new ArrayList<>();
            filters.add(cb.isTrue(root.get("availability")));
            if (location != null && !location.isBlank()) {
                String searchPattern = "%" + location.toLowerCase() + "%";
                filters.add(cb.or(cb.like(cb.lower(root.get("location")), searchPattern),
                    cb.like(cb.lower(root.get("city")), searchPattern), cb.like(cb.lower(root.get("state")), searchPattern)));
            }
            if (type != null) filters.add(cb.equal(root.get("propertyType"), type));
            if (minRent != null) filters.add(cb.greaterThanOrEqualTo(root.get("monthlyRent"), minRent));
            if (maxRent != null) filters.add(cb.lessThanOrEqualTo(root.get("monthlyRent"), maxRent));
            return cb.and(filters.toArray(Predicate[]::new));
        };
        return properties.findAll(spec);
    }

    @Transactional
    public Property create(PropertyRequest request, String email) {
        User owner = users.findByEmail(email);
        if (owner.getRole() != Role.OWNER && owner.getRole() != Role.ADMIN) throw new AccessDeniedException("Owners only");
        Property property = new Property();
        property.setOwner(owner);
        apply(property, request);
        return properties.save(property);
    }

    @Transactional
    public Property update(Long id, PropertyRequest request, String email) {
        Property property = find(id);
        authorize(property, email);
        apply(property, request);
        return properties.save(property);
    }

    @Transactional
    public Property availability(Long id, boolean available, String email) {
        Property property = find(id);
        authorize(property, email);
        property.setAvailability(available);
        return properties.save(property);
    }

    @Transactional
    public void delete(Long id, String email) { Property property = find(id); authorize(property, email); properties.delete(property); }

    private void authorize(Property property, String email) {
        User user = users.findByEmail(email);
        if (user.getRole() != Role.ADMIN && !property.getOwner().getId().equals(user.getId())) throw new AccessDeniedException("You do not own this property");
    }

    private void apply(Property p, PropertyRequest r) {
        p.setTitle(r.title().trim()); p.setDescription(r.description().trim()); p.setAddress(r.address().trim());
        p.setCity(r.city().trim()); p.setState(r.state().trim()); p.setLocation(r.location().trim());
        p.setPropertyType(r.propertyType()); p.setMonthlyRent(r.monthlyRent()); p.setSecurityDeposit(r.securityDeposit());
        p.setBedrooms(r.bedrooms()); p.setBathrooms(r.bathrooms()); p.setArea(r.area());
        p.setFurnishingStatus(r.furnishingStatus()); p.setAvailability(r.availability() == null || r.availability());
        p.setImageUrl(r.imageUrl());
    }
}