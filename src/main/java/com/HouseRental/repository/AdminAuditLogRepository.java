package com.HouseRental.repository;

import com.HouseRental.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {
}
