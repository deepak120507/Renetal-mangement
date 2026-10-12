package com.rentalhouse.service;

import com.rentalhouse.repository.ContactMessageRepository;
import com.rentalhouse.repository.PropertyRepository;
import com.rentalhouse.repository.RentalRequestRepository;
import com.rentalhouse.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminService {
    private final UserRepository users;
    private final PropertyRepository properties;
    private final RentalRequestRepository requests;
    private final ContactMessageRepository contacts;

    public AdminService(UserRepository users, PropertyRepository properties, RentalRequestRepository requests, ContactMessageRepository contacts) {
        this.users = users; this.properties = properties; this.requests = requests; this.contacts = contacts;
    }
    public Map<String, Long> dashboard() {
        return Map.of("users", users.count(), "properties", properties.count(), "rentalRequests", requests.count(), "contactMessages", contacts.count());
    }
}