package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "TaskResponse", description = "Task response DTO")
public record TaskResponse(
        @Schema(example = "2f6c9dd7-3dd0-4b25-ae6f-0cf6fce22c32")
        UUID id,

        @Schema(example = "Task title")
        String title,

        @Schema(example = "Task description content")
        String description,

        @Schema(example = "HIGH")
        TaskPriority priority,

        @Schema(example = "2026-02-10")
        LocalDate dueDate,

        @Schema(description = "Status code identifier", example = "IN_DEVELOPMENT")
        String statusCode,

        @Schema(description = "User-friendly status label", example = "In Development")
        String statusLabel,

        @Schema(example = "7e2b9b1d-4af0-4f1d-9b20-8a8d5f77f49d")
        UUID projectId,

        @Schema(example = "96cd0fbb-6bee-42ed-be77-e9cf64d6215b")
        UUID createdById,

        @Schema(example = "d43376db-aca0-4db7-a1be-25fae98951f9")
        UUID assigneeId,

        @Schema(example = "2026-01-27T00:40:34.932Z")
        Instant createdAt,

        @Schema(example = "2026-01-27T00:41:12.011Z")
        Instant updatedAt
) {
}
