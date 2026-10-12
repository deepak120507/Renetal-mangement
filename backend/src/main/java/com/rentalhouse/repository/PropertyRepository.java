package com.rentalhouse.repository;

import com.rentalhouse.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findByAvailabilityTrueOrderByIdDesc();
    List<Property> findByOwnerIdOrderByIdDesc(Long ownerId);
}