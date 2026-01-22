package com.example.task_management_system.project.dto;

public record ProjectCreateRequest(
        String key,
        String name,
        String description
) {
}
