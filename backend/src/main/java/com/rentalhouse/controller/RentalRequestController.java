package com.rentalhouse.controller;

import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.entity.DomainEnums.RequestStatus;
import com.rentalhouse.service.RentalRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-requests")
public class RentalRequestController {
    private final RentalRequestService requests;
    public RentalRequestController(RentalRequestService requests) { this.requests = requests; }

    @GetMapping
    public List<RentalRequestView> all(Authentication auth) { return requests.visible(auth.getName()).stream().map(RentalRequestView::from).toList(); }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('TENANT')")
    public List<RentalRequestView> mine(Authentication auth) { return requests.mine(auth.getName()).stream().map(RentalRequestView::from).toList(); }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public List<RentalRequestView> owner(Authentication auth) { return requests.ownerRequests(auth.getName()).stream().map(RentalRequestView::from).toList(); }

    @GetMapping("/{id}")
    public RentalRequestView get(@PathVariable Long id, Authentication auth) { return RentalRequestView.from(requests.findAuthorized(id, auth.getName())); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(@Valid @RequestBody RentalRequestRequest request, Authentication auth) {
        return new ApiResponse(true, "Rental request created", RentalRequestView.from(requests.create(request, auth.getName())));
    }

    @PutMapping("/{id}/accept")
    public ApiResponse accept(@PathVariable Long id, Authentication auth) { return change(id, RequestStatus.ACCEPTED, auth); }
    @PutMapping("/{id}/reject")
    public ApiResponse reject(@PathVariable Long id, Authentication auth) { return change(id, RequestStatus.REJECTED, auth); }
    @PutMapping("/{id}/cancel")
    public ApiResponse cancel(@PathVariable Long id, Authentication auth) { return change(id, RequestStatus.CANCELLED, auth); }
    @PutMapping("/{id}/status")
    public ApiResponse status(@PathVariable Long id, @RequestParam RequestStatus status, Authentication auth) { return change(id, status, auth); }

    private ApiResponse change(Long id, RequestStatus status, Authentication auth) {
        return new ApiResponse(true, "Request status updated", RentalRequestView.from(requests.setStatus(id, status, auth.getName())));
    }
}