package com.example.task_management_system.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @NotBlank @Size(max = 20) String key,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description
) {
}
