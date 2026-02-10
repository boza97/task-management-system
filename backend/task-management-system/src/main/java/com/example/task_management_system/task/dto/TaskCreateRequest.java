package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "TaskCreateRequest", description = "Payload for creating a new task")
public record TaskCreateRequest(

        @Schema(
                description = "Project ID where the task will be created",
                example = "7e2b9b1d-4af0-4f1d-9b20-8a8d5f77f49d",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        UUID projectId,

        @Schema(
                description = "Task title",
                example = "Task 1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 200)
        String title,

        @Schema(
                description = "Task description (optional)",
                example = "Task description content",
                maxLength = 5000
        )
        @Size(max = 5000)
        String description,

        @Schema(
                description = "Task priority",
                example = "HIGH",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        TaskPriority priority,

        @Schema(
                description = "Due date (optional)",
                example = "2026-02-10"
        )
        LocalDate dueDate,

        @Schema(
                description = "Assignee user ID (optional). If null then unassigned.",
                example = "d43376db-aca0-4db7-a1be-25fae98951f9"
        )
        UUID assigneeId
) {
}
