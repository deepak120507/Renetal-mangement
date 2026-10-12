package com.rentalhouse.repository;

import com.rentalhouse.entity.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {
    List<RentalRequest> findByTenantIdOrderByRequestDateDesc(Long tenantId);
    List<RentalRequest> findByPropertyOwnerIdOrderByRequestDateDesc(Long ownerId);
    boolean existsByTenantIdAndPropertyIdAndStatus(Long tenantId, Long propertyId, com.rentalhouse.entity.DomainEnums.RequestStatus status);
}