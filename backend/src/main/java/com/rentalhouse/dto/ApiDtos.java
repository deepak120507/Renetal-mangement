package com.rentalhouse.dto;

import com.rentalhouse.entity.ContactMessage;
import com.rentalhouse.entity.DomainEnums.*;
import com.rentalhouse.entity.Property;
import com.rentalhouse.entity.RentalRequest;
import com.rentalhouse.entity.User;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public final class ApiDtos {
    private ApiDtos() {}

    public record ApiResponse(boolean success, String message, Object data) {}
    public record AuthRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String fullName, @NotBlank @Email String email,
                                  @NotBlank @Pattern(regexp = "^[0-9+()\\-\\s]{7,18}$") String phone,
                                  @NotBlank @Size(min = 6) String password, @NotBlank String confirmPassword,
                                  @NotNull Role role, String address) {}
    public record AuthResponse(boolean success, String message, String token, UserView user) {}
    public record UserView(Long id, String fullName, String email, String phone, String address, Role role, UserStatus status) {
        public static UserView from(User user) {
            return new UserView(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getRole(), user.getStatus());
        }
    }
    public record UserUpdateRequest(String fullName, String phone, String address, String password) {}
    public record PropertyRequest(@NotBlank String title, @NotBlank String description, @NotBlank String address,
                                 @NotBlank String city, @NotBlank String state, @NotBlank String location,
                                 @NotNull PropertyType propertyType, @NotNull @Positive BigDecimal monthlyRent,
                                 @NotNull @PositiveOrZero BigDecimal securityDeposit, @NotNull @PositiveOrZero Integer bedrooms,
                                 @NotNull @PositiveOrZero Integer bathrooms, @NotNull @Positive Integer area,
                                 @NotNull FurnishingStatus furnishingStatus, Boolean availability, String imageUrl) {}
    public record PropertyView(Long id, String title, String description, String address, String city, String state,
                               String location, PropertyType propertyType, BigDecimal monthlyRent, BigDecimal securityDeposit,
                               Integer bedrooms, Integer bathrooms, Integer area, FurnishingStatus furnishingStatus,
                               boolean availability, String imageUrl, Long ownerId, String ownerName, String ownerEmail, String ownerPhone) {
        public static PropertyView from(Property property) {
            User owner = property.getOwner();
            return new PropertyView(property.getId(), property.getTitle(), property.getDescription(), property.getAddress(),
                    property.getCity(), property.getState(), property.getLocation(), property.getPropertyType(),
                    property.getMonthlyRent(), property.getSecurityDeposit(), property.getBedrooms(), property.getBathrooms(),
                    property.getArea(), property.getFurnishingStatus(), property.isAvailability(), property.getImageUrl(),
                    owner.getId(), owner.getFullName(), owner.getEmail(), owner.getPhone());
        }
    }
    public record RentalRequestRequest(@NotNull Long propertyId, @NotNull @FutureOrPresent LocalDate moveInDate, @NotBlank String message) {}
    public record RentalRequestView(Long id, Long tenantId, String tenantName, String tenantEmail,
                                    PropertyView property, Instant requestDate, LocalDate moveInDate,
                                    String message, RequestStatus status) {
        public static RentalRequestView from(RentalRequest request) {
            User tenant = request.getTenant();
            return new RentalRequestView(request.getId(), tenant.getId(), tenant.getFullName(), tenant.getEmail(),
                    PropertyView.from(request.getProperty()), request.getRequestDate(), request.getMoveInDate(),
                    request.getMessage(), request.getStatus());
        }
    }
    public record ContactMessageRequest(@NotNull Long propertyId, @NotBlank String message) {}
    public record ContactView(Long id, Long tenantId, String tenantName, Long ownerId, String ownerName,
                              Long propertyId, String propertyTitle, String message, Instant createdAt, ContactStatus status) {
        public static ContactView from(ContactMessage contact) {
            return new ContactView(contact.getId(), contact.getTenant().getId(), contact.getTenant().getFullName(),
                    contact.getOwner().getId(), contact.getOwner().getFullName(), contact.getProperty().getId(),
                    contact.getProperty().getTitle(), contact.getMessage(), contact.getCreatedAt(), contact.getStatus());
        }
    }
}