package com.example.task_management_system.task.audit;

import com.example.task_management_system.task.audit.dto.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLogResponse> getTaskAuditLogs(UUID taskId) {
        return auditLogRepository.findAllByTaskIdWithUser(taskId).stream().map(this::mapToResponse)
                                 .toList();

    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return new AuditLogResponse(auditLog.getId(), auditLog.getActionType(), auditLog.getTimestamp(),
                                    auditLog.getOldValue(), auditLog.getNewValue(), auditLog.getPerformedBy().getId(),
                                    auditLog.getPerformedBy().getFirstName() + " " +
                                    auditLog.getPerformedBy().getLastName());
    }
}
