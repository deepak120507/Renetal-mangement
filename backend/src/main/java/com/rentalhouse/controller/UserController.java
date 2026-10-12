package com.rentalhouse.controller;

import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.entity.User;
import com.rentalhouse.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;
    public UserController(UserService users) { this.users = users; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserView> getAll() { return users.findAll().stream().map(UserView::from).toList(); }

    @GetMapping("/me")
    public UserView me(Authentication authentication) { return UserView.from(users.findByEmail(authentication.getName())); }

    @GetMapping("/{id}")
    public UserView getById(@PathVariable Long id, Authentication authentication) {
        User user = users.findById(id);
        User current = users.findByEmail(authentication.getName());
        if (!Objects.equals(user.getId(), current.getId()) && current.getRole() != com.rentalhouse.entity.DomainEnums.Role.ADMIN) {
            throw new AccessDeniedException("Not allowed");
        }
        return UserView.from(user);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @RequestBody UserUpdateRequest request, Authentication authentication) {
        return new ApiResponse(true, "Profile updated", UserView.from(users.update(id, request, authentication.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse delete(@PathVariable Long id) {
        users.delete(id);
        return new ApiResponse(true, "User deleted", null);
    }
}