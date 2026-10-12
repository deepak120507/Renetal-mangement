package com.rentalhouse.entity;

import com.rentalhouse.entity.DomainEnums.FurnishingStatus;
import com.rentalhouse.entity.DomainEnums.PropertyType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "properties")
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(nullable = false) private String address;
    @Column(nullable = false) private String city;
    @Column(nullable = false) private String state;
    @Column(nullable = false) private String location;
    @Enumerated(EnumType.STRING) @Column(name = "property_type", nullable = false) private PropertyType propertyType;
    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2) private BigDecimal monthlyRent;
    @Column(name = "security_deposit", nullable = false, precision = 10, scale = 2) private BigDecimal securityDeposit;
    @Column(nullable = false) private Integer bedrooms;
    @Column(nullable = false) private Integer bathrooms;
    @Column(nullable = false) private Integer area;
    @Enumerated(EnumType.STRING) @Column(name = "furnishing_status", nullable = false) private FurnishingStatus furnishingStatus;
    @Column(nullable = false) private boolean availability = true;
    @Column(name = "image_url", length = 500) private String imageUrl;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    public BigDecimal getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }
    public BigDecimal getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(BigDecimal securityDeposit) { this.securityDeposit = securityDeposit; }
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }
    public FurnishingStatus getFurnishingStatus() { return furnishingStatus; }
    public void setFurnishingStatus(FurnishingStatus furnishingStatus) { this.furnishingStatus = furnishingStatus; }
    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}