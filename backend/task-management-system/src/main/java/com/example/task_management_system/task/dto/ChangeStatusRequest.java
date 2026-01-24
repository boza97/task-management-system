package com.example.task_management_system.task.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeStatusRequest(
        @NotBlank String statusCode
) {
}
