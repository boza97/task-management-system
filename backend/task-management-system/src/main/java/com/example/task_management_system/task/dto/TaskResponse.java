package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskPriority priority,
        LocalDate dueDate,
        String statusCode,
        String statusLabel,
        UUID projectId,
        UUID createdById,
        UUID assigneeId,
        Instant createdAt,
        Instant updatedAt
) {
}
