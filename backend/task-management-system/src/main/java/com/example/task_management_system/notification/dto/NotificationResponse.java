package com.example.task_management_system.notification.dto;

import com.example.task_management_system.notification.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String message,
        UUID taskId,
        UUID projectId,
        boolean read,
        Instant createdAt
) {
}
