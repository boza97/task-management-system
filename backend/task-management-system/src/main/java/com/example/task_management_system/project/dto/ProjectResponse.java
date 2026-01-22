package com.example.task_management_system.project.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String key,
        String name,
        String description,
        UUID ownerId,
        String ownerFullName,
        Instant createdAt,
        Instant updatedAt
) {
}
