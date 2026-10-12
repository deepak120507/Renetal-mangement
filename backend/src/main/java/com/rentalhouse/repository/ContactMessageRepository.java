package com.rentalhouse.repository;

import com.rentalhouse.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    List<ContactMessage> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
}