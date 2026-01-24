package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;

import java.time.LocalDate;
import java.util.UUID;

public record TaskCreateRequest(
        UUID projectId,
        String title,
        String description,
        TaskPriority priority,
        LocalDate dueDate,
        UUID assigneeId
) {
}
