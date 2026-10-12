package com.rentalhouse.controller;

import com.rentalhouse.config.JwtService;
import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.entity.User;
import com.rentalhouse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService users;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService users, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users; this.authenticationManager = authenticationManager; this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@Valid @RequestBody RegisterRequest request) {
        return new ApiResponse(true, "User registered successfully", UserView.from(users.register(request)));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = users.findByEmail(principal.getUsername());
        return new AuthResponse(true, "Login successful", jwtService.generateToken(principal), UserView.from(user));
    }

    @PostMapping("/logout")
    public ApiResponse logout() { return new ApiResponse(true, "Logged out successfully", null); }
}