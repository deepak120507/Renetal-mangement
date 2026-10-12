package com.rentalhouse.service;

import com.rentalhouse.dto.ApiDtos.ContactMessageRequest;
import com.rentalhouse.entity.ContactMessage;
import com.rentalhouse.entity.DomainEnums.Role;
import com.rentalhouse.entity.Property;
import com.rentalhouse.entity.User;
import com.rentalhouse.repository.ContactMessageRepository;
import com.rentalhouse.repository.PropertyRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rentalhouse.exception.AppExceptions.ResourceNotFoundException;

@Service
public class ContactMessageService {
    private final ContactMessageRepository contacts;
    private final PropertyRepository properties;
    private final UserService users;

    public ContactMessageService(ContactMessageRepository contacts, PropertyRepository properties, UserService users) {
        this.contacts = contacts; this.properties = properties; this.users = users;
    }
    public List<ContactMessage> visible(String email) {
        User user = users.findByEmail(email);
        return switch (user.getRole()) {
            case ADMIN -> contacts.findAll();
            case OWNER -> contacts.findByOwnerIdOrderByCreatedAtDesc(user.getId());
            case TENANT -> contacts.findByTenantIdOrderByCreatedAtDesc(user.getId());
        };
    }
    @Transactional
    public ContactMessage create(ContactMessageRequest body, String email) {
        User tenant = users.findByEmail(email);
        if (tenant.getRole() != Role.TENANT) throw new AccessDeniedException("Tenants only");
        Property property = properties.findById(body.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        ContactMessage contact = new ContactMessage();
        contact.setTenant(tenant); contact.setOwner(property.getOwner()); contact.setProperty(property); contact.setMessage(body.message().trim());
        return contacts.save(contact);
    }
}