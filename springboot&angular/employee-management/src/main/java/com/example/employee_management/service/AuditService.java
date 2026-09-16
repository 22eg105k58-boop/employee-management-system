package com.example.employee_management.service;

import com.example.employee_management.entity.AuditLog;
import com.example.employee_management.repository.AuditLogRepository;
import com.example.employee_management.security.AuditAction;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            String username,
            AuditAction action,
            Long employeeId,
            String details) {

        AuditLog auditLog = new AuditLog(
                username,
                action,
                employeeId,
                details,
                LocalDateTime.now()
        );

        auditLogRepository.save(auditLog);
    }
}