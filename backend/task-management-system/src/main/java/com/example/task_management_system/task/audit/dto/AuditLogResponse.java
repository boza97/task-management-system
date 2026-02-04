package com.example.task_management_system.task.audit.dto;

import com.example.task_management_system.task.audit.ActionType;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        ActionType actionType,
        Instant timestamp,
        String oldValue,
        String newValue,
        UUID performedById,
        String performedByName
) {
}
