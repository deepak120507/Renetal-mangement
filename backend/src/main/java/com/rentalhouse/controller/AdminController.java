package com.rentalhouse.controller;

import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.service.AdminService;
import com.rentalhouse.service.PropertyService;
import com.rentalhouse.service.RentalRequestService;
import com.rentalhouse.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService admin;
    private final UserService users;
    private final PropertyService properties;
    private final RentalRequestService requests;

    public AdminController(AdminService admin, UserService users, PropertyService properties, RentalRequestService requests) {
        this.admin = admin; this.users = users; this.properties = properties; this.requests = requests;
    }

    @GetMapping("/dashboard")
    public Map<String, Long> dashboard() { return admin.dashboard(); }
    @GetMapping("/users")
    public List<UserView> users() { return users.findAll().stream().map(UserView::from).toList(); }
    @GetMapping("/properties")
    public List<PropertyView> properties() { return properties.all().stream().map(PropertyView::from).toList(); }
    @GetMapping("/rental-requests")
    public List<RentalRequestView> requests() { return requests.visible("admin@rentalhouse.com").stream().map(RentalRequestView::from).toList(); }
}