package com.rentalhouse.service;

import com.rentalhouse.dto.ApiDtos.RentalRequestRequest;
import com.rentalhouse.entity.DomainEnums.RequestStatus;
import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.entity.Property;
import com.rentalhouse.entity.RentalRequest;
import com.rentalhouse.entity.User;
import com.rentalhouse.repository.PropertyRepository;
import com.rentalhouse.repository.RentalRequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rentalhouse.exception.AppExceptions.*;

@Service
public class RentalRequestService {
    private final RentalRequestRepository requests;
    private final PropertyRepository properties;
    private final UserService users;

    public RentalRequestService(RentalRequestRepository requests, PropertyRepository properties, UserService users) {
        this.requests = requests; this.properties = properties; this.users = users;
    }

    public List<RentalRequest> visible(String email) {
        User user = users.findByEmail(email);
        return switch (user.getRole()) {
            case ADMIN -> requests.findAll();
            case OWNER -> requests.findByPropertyOwnerIdOrderByRequestDateDesc(user.getId());
            case TENANT -> requests.findByTenantIdOrderByRequestDateDesc(user.getId());
        };
    }
    public List<RentalRequest> mine(String email) { return requests.findByTenantIdOrderByRequestDateDesc(users.findByEmail(email).getId()); }
    public List<RentalRequest> ownerRequests(String email) { return requests.findByPropertyOwnerIdOrderByRequestDateDesc(users.findByEmail(email).getId()); }
    public RentalRequest findAuthorized(Long id, String email) {
        RentalRequest request = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental request not found"));
        User user = users.findByEmail(email);
        if (user.getRole() != Role.ADMIN && !request.getTenant().getId().equals(user.getId()) && !request.getProperty().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot view this rental request");
        }
        return request;
    }

    @Transactional
    public RentalRequest create(RentalRequestRequest body, String email) {
        User tenant = users.findByEmail(email);
        if (tenant.getRole() != Role.TENANT) throw new AccessDeniedException("Tenants only");
        Property property = properties.findById(body.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!property.isAvailability()) throw new InvalidRequestException("This property is not available");
        if (property.getOwner().getId().equals(tenant.getId())) throw new InvalidRequestException("You cannot request your own property");
        if (requests.existsByTenantIdAndPropertyIdAndStatus(tenant.getId(), property.getId(), RequestStatus.PENDING)) {
            throw new InvalidRequestException("You already have a pending request for this property");
        }
        RentalRequest request = new RentalRequest();
        request.setTenant(tenant); request.setProperty(property); request.setMoveInDate(body.moveInDate()); request.setMessage(body.message().trim());
        return requests.save(request);
    }

    @Transactional
    public RentalRequest setStatus(Long id, RequestStatus status, String email) {
        RentalRequest request = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rental request not found"));
        User user = users.findByEmail(email);
        if (status == RequestStatus.CANCELLED) {
            if (user.getRole() != Role.TENANT || !request.getTenant().getId().equals(user.getId())) throw new AccessDeniedException("Only the tenant can cancel this request");
            if (request.getStatus() != RequestStatus.PENDING) throw new InvalidRequestException("Only pending requests can be cancelled");
        } else {
            if (user.getRole() != Role.ADMIN && (user.getRole() != Role.OWNER || !request.getProperty().getOwner().getId().equals(user.getId()))) {
                throw new AccessDeniedException("Only the property owner or admin can update this request");
            }
            if (request.getStatus() != RequestStatus.PENDING) throw new InvalidRequestException("Only pending requests can be updated");
        }
        request.setStatus(status);
        if (status == RequestStatus.ACCEPTED) request.getProperty().setAvailability(false);
        return requests.save(request);
    }
}