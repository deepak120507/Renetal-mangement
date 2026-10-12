package com.rentalhouse.service;

import com.rentalhouse.dto.ApiDtos.RegisterRequest;
import com.rentalhouse.dto.ApiDtos.UserUpdateRequest;
import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.entity.DomainEnums.UserStatus;
import com.rentalhouse.entity.User;
import com.rentalhouse.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.rentalhouse.exception.AppExceptions.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }

    @Transactional
    public User register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) throw new InvalidRequestException("Public admin registration is not allowed");
        if (!request.password().equals(request.confirmPassword())) throw new InvalidRequestException("Passwords do not match");
        if (users.existsByEmail(request.email())) throw new DuplicateEmailException("Email is already registered");
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone().trim());
        user.setPassword(encoder.encode(request.password()));
        user.setAddress(request.address());
        user.setRole(request.role());
        user.setStatus(UserStatus.ACTIVE);
        return users.save(user);
    }

    public User findByEmail(String email) {
        return users.findByEmail(email.toLowerCase()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    public User findById(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")); }
    public List<User> findAll() { return users.findAll(); }

    @Transactional
    public User update(Long id, UserUpdateRequest request, String currentEmail) {
        User current = findByEmail(currentEmail);
        if (!Objects.equals(current.getId(), id) && current.getRole() != Role.ADMIN) throw new org.springframework.security.access.AccessDeniedException("Not allowed");
        User user = findById(id);
        if (request.fullName() != null && !request.fullName().isBlank()) user.setFullName(request.fullName().trim());
        if (request.phone() != null && !request.phone().isBlank()) user.setPhone(request.phone().trim());
        if (request.address() != null) user.setAddress(request.address().trim());
        if (request.password() != null && !request.password().isBlank()) user.setPassword(encoder.encode(request.password()));
        return users.save(user);
    }

    public void delete(Long id) { users.delete(findById(id)); }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = users.findByEmail(email.toLowerCase()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).roles(user.getRole().name())
                .disabled(user.getStatus() != UserStatus.ACTIVE).build();
    }
}