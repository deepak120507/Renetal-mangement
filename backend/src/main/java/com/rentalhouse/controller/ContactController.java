package com.rentalhouse.controller;

import com.rentalhouse.dto.ApiDtos.*;
import com.rentalhouse.service.ContactMessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactMessageService contacts;
    public ContactController(ContactMessageService contacts) { this.contacts = contacts; }

    @GetMapping
    public List<ContactView> all(Authentication auth) { return contacts.visible(auth.getName()).stream().map(ContactView::from).toList(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(@Valid @RequestBody ContactMessageRequest request, Authentication auth) {
        return new ApiResponse(true, "Message sent", ContactView.from(contacts.create(request, auth.getName())));
    }
}