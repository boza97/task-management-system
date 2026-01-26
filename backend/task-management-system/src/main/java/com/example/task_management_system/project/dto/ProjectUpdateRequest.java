package com.example.task_management_system.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description
) {
}
