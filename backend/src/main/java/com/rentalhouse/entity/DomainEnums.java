package com.rentalhouse.entity;

public final class DomainEnums {
    private DomainEnums() {}

    public enum Role { TENANT, OWNER, ADMIN }
    public enum UserStatus { ACTIVE, INACTIVE, DELETED }
    public enum PropertyType { APARTMENT, HOUSE, VILLA, STUDIO, DUPLEX, COMMERCIAL }
    public enum FurnishingStatus { FURNISHED, SEMI_FURNISHED, UNFURNISHED }
    public enum RequestStatus { PENDING, ACCEPTED, REJECTED, CANCELLED }
    public enum ContactStatus { NEW, READ, REPLIED }
}