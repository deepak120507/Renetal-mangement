package com.rentalhouse.config;

import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.entity.DomainEnums.UserStatus;
import com.rentalhouse.entity.User;
import com.rentalhouse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        User admin = users.findByEmail("admin@rentalhouse.com").orElseGet(User::new);
        admin.setFullName("System Administrator");
        admin.setEmail("admin@rentalhouse.com");
        admin.setPhone("+1234567890");
        admin.setAddress("Rental House Management Office");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        if (admin.getId() == null || !encoder.matches("admin123", admin.getPassword())) {
            admin.setPassword(encoder.encode("admin123"));
        }
        users.save(admin);
        System.out.println("Local admin ready: admin@rentalhouse.com / admin123 (change before deployment)");
    }
}