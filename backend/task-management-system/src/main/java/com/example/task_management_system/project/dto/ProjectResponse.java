package com.example.task_management_system.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "ProjectResponse", description = "Project response DTO")
public record ProjectResponse(

        @Schema(example = "7e2b9b1d-4af0-4f1d-9b20-8a8d5f77f49d")
        UUID id,

        @Schema(example = "TMS")
        String key,

        @Schema(example = "Task Management System")
        String name,

        @Schema(example = "Task management system project description")
        String description,

        @Schema(example = "asd1231as-4af0-4f1d-9b20-8a8d5f77f49d")
        UUID ownerId,

        @Schema(example = "Bozidar Mastilovic")
        String ownerFullName,

        @Schema(example = "2026-01-27T00:40:34.932Z")
        Instant createdAt,

        @Schema(example = "2026-01-29T00:40:34.932Z")
        Instant updatedAt
) {
}
