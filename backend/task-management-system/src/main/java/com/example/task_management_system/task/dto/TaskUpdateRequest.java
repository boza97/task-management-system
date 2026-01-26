package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(
        @Size(max = 200) String title,
        @Size(max = 500) String description,
        TaskPriority priority,
        LocalDate dueDate
) {
}