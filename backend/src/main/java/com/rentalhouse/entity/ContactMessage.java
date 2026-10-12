package com.rentalhouse.entity;

import com.rentalhouse.entity.DomainEnums.ContactStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "tenant_id", nullable = false) private User tenant;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "property_id", nullable = false) private Property property;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ContactStatus status = ContactStatus.NEW;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = Instant.now(); }

    public Long getId() { return id; }
    public User getTenant() { return tenant; }
    public void setTenant(User tenant) { this.tenant = tenant; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getCreatedAt() { return createdAt; }
    public DomainEnums.ContactStatus getStatus() { return status; }
    public void setStatus(DomainEnums.ContactStatus status) { this.status = status; }
}