package com.rentalhouse.entity;

import com.rentalhouse.entity.DomainEnums.RequestStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "rental_requests")
public class RentalRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "tenant_id", nullable = false) private User tenant;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "property_id", nullable = false) private Property property;
    @Column(name = "request_date", nullable = false, updatable = false) private Instant requestDate;
    @Column(name = "move_in_date", nullable = false) private LocalDate moveInDate;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RequestStatus status = RequestStatus.PENDING;

    @PrePersist
    void onCreate() { if (requestDate == null) requestDate = Instant.now(); }

    public Long getId() { return id; }
    public User getTenant() { return tenant; }
    public void setTenant(User tenant) { this.tenant = tenant; }
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    public Instant getRequestDate() { return requestDate; }
    public LocalDate getMoveInDate() { return moveInDate; }
    public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
}