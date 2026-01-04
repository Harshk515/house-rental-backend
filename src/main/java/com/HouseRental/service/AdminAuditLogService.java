package com.HouseRental.service;

import com.HouseRental.entity.AdminAuditLog;
import com.HouseRental.repository.AdminAuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminAuditLogService {

    private final AdminAuditLogRepository adminAuditLogRepository;

    public AdminAuditLogService(AdminAuditLogRepository adminAuditLogRepository) {
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    // Log any admin action
    public void log(
            String adminEmail,
            String action,
            String targetType,
            Long targetId
    ) {
        AdminAuditLog log = new AdminAuditLog();
        log.setAdminEmail(adminEmail);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setCreatedAt(LocalDateTime.now());
        adminAuditLogRepository.save(log);
    }

    // (optional) view logs
    public List<AdminAuditLog> getAllLogs() {
        return adminAuditLogRepository.findAll();
    }
}
