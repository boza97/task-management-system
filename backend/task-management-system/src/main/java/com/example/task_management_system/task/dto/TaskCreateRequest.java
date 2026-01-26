package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record TaskCreateRequest(
        @NotNull UUID projectId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 500) String description,
        @NotNull TaskPriority priority,
        LocalDate dueDate,
        UUID assigneeId
) {
}
