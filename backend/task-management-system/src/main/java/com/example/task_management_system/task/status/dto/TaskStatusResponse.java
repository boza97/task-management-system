package com.example.task_management_system.task.status.dto;

public record TaskStatusResponse(
        String code,
        String name,
        int displayOrder
) {
}
