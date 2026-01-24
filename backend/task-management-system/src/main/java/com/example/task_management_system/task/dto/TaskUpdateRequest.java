package com.example.task_management_system.task.dto;

import com.example.task_management_system.task.TaskPriority;

import java.time.LocalDate;

public record TaskUpdateRequest(
        String title,
        String description,
        TaskPriority priority,
        LocalDate dueDate
) {
}