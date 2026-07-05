package com.devflowai.service;

import com.devflowai.dto.response.AuditLogResponse;
import com.devflowai.entity.AuditLog;
import com.devflowai.entity.User;
import com.devflowai.repository.AuditLogRepository;
import com.devflowai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .details(log.getDetails())
                .userEmail(log.getUser().getEmail())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public void logAction(User user, String action, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .details(details)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLogResponse> getMyLogs(String email) {
        User user = getUserByEmail(email);
        return auditLogRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }
}
