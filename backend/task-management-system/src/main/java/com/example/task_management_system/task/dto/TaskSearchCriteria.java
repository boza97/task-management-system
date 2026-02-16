package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "TaskSearchCriteria", description = "Params for searching tasks")
public record TaskSearchCriteria(
        @Schema(example = "task name", description = "search by title")
        String search,
        @Schema(example = "LOW", description = "search by priority")
        TaskPriority priority,
        @Schema(example = "TO_DO", description = "search by status code")
        String statusCode,
        @Schema(example = "d43376db-aca0-4db7-a1be-25fae98951f9", description = "search by assignee")
        UUID assigneeId,
        @Schema(example = "2026-02-10", description = "search by due date from")
        LocalDate dueDateFrom,
        @Schema(example = "2026-02-10", description = "search by due date to")
        LocalDate dueDateTo
) {
}
